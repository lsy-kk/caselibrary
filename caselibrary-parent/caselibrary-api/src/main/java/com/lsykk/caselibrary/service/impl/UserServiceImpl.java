package com.lsykk.caselibrary.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lsykk.caselibrary.dao.mapper.UserMapper;
import com.lsykk.caselibrary.dao.pojo.User;
import com.lsykk.caselibrary.dao.repository.UserVoRepository;
import com.lsykk.caselibrary.service.LoginService;
import com.lsykk.caselibrary.service.NoticeService;
import com.lsykk.caselibrary.service.UserService;
import com.lsykk.caselibrary.utils.DateUtils;
import com.lsykk.caselibrary.vo.ApiResult;
import com.lsykk.caselibrary.vo.ErrorCode;
import com.lsykk.caselibrary.vo.PageVo;
import com.lsykk.caselibrary.vo.UserVo;
import com.lsykk.caselibrary.vo.params.PageParams;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;


@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private UserVoRepository userVoRepository;
    @Autowired
    private ElasticsearchRestTemplate elasticsearchRestTemplate;
    @Autowired
    private LoginService loginService;
    @Autowired
    private NoticeService noticeService;
    @Autowired
    private RedisTemplate<String,String> redisTemplate;


    @Override
    public ApiResult findUserByToken(String token) {
        /**
         * 1. token合法性校验
         *    是否为空，解析是否成功 redis是否存在
         * 2. 如果校验失败 返回错误
         * 3. 如果成功，返回对应的结果 UserVo
         */
        User user = loginService.checkToken(token);
        if (user == null){
            return ApiResult.fail(ErrorCode.TOKEN_ERROR.getCode(),ErrorCode.TOKEN_ERROR.getMsg());
        }
        UserVo UserVo = copy(user);
        return ApiResult.success(UserVo);
    }

    @Override
    public ApiResult getUserList(PageParams pageParams, User user){
        /* 分页查询 user数据库表 */
        Page<User> page = new Page<>(pageParams.getPage(), pageParams.getPageSize());
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        /* 动态SQL语句 */
        queryWrapper.eq(user.getId()!=null, User::getId, user.getId());
        queryWrapper.like(StringUtils.isNotBlank(user.getEmail()), User::getEmail, user.getEmail());
        queryWrapper.eq(user.getAuthority()!=null, User::getAuthority, user.getAuthority());
        /* 按照ID顺序排序 */
        queryWrapper.orderByAsc(User::getId);
        Page<User> userPage = userMapper.selectPage(page, queryWrapper);
        List<User> userList = userPage.getRecords();
        return ApiResult.success(userList);
    }

    @Override
    public ApiResult getSearchList(PageParams pageParams, String keyWords){
        QueryBuilder queryBuilder = QueryBuilders.boolQuery()
                .must(QueryBuilders.boolQuery()
                        .should(QueryBuilders.matchQuery("username", keyWords)))
                .filter(QueryBuilders.termQuery("status",1));
        NativeSearchQueryBuilder searchQueryBuilder = new NativeSearchQueryBuilder()
                .withPageable(PageRequest.of(pageParams.getPage()-1, pageParams.getPageSize()))
                .withQuery(queryBuilder)
                .withHighlightFields(
                        new HighlightBuilder.Field("username"))
                .withHighlightBuilder(new HighlightBuilder().preTags("<span style='color:red'>").postTags("</span>"));
        NativeSearchQuery searchQuery = searchQueryBuilder.build();
        SearchHits<UserVo> search = elasticsearchRestTemplate.search(searchQuery, UserVo.class);
        List<SearchHit<UserVo>> searchHits = search.getSearchHits();
        List<UserVo> userVoList = new ArrayList<>();
        for (SearchHit<UserVo> searchHit : searchHits) {
            Map<String, List<String>> highlightFields = searchHit.getHighlightFields();
            searchHit.getContent().setUsername(
                    highlightFields.get("username") == null ? searchHit.getContent().getUsername() : highlightFields.get("username").get(0));
            userVoList.add(searchHit.getContent());
        }
        PageVo<UserVo> pageVo = new PageVo();
        pageVo.setRecordList(userVoList);
        pageVo.setTotal(search.getTotalHits());
        return ApiResult.success(pageVo);
    }

    @Override
    public ApiResult userVoRepositoryReload(){
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        List<User> userList = userMapper.selectList(queryWrapper);
        for (User user: userList){
            userVoRepository.save(copy(user));
        }
        return ApiResult.success();
    }
    @Override
    public User findUserById(Long id) {
        return userMapper.selectById(id);
    }

    @Override
    public UserVo findUserVoById(Long id) {
        return copy(userMapper.selectById(id));
    }

    @Override
    public User findUserByEmail(String email) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getEmail, email);
        queryWrapper.last("limit 1");
        return userMapper.selectOne(queryWrapper);
    }

    @Override
    public User findUserByEmailAndPassword(String email, String password) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getEmail, email);
        queryWrapper.eq(User::getPassword, password);
        // 必须是启用状态的用户
        queryWrapper.eq(User::getStatus, 1);
        queryWrapper.last("limit 1");
        return userMapper.selectOne(queryWrapper);
    }

    @Override
    public void saveUser(User user) {
        userMapper.insert(user);
        userVoRepository.save(copy(user));
    }

    @Override
    public ApiResult updatePassword(User user){
        /* 这里的user密码没有加密，需要加密保存 */
        String enPassword = loginService.encryptedPassword(user.getPassword());
        LambdaUpdateWrapper<User> userUpdateWrapper = new LambdaUpdateWrapper<>();
        userUpdateWrapper.set(User::getPassword, enPassword);
        userUpdateWrapper.eq(User::getId, user.getId());
        userMapper.update(null, userUpdateWrapper);
        return ApiResult.success();
    }

    @Override
    public ApiResult updateUser(User user){
        /* 检查参数是否合法 */
        if (user.getId() == null
            || StringUtils.isBlank(user.getEmail())
            || StringUtils.isBlank(user.getUsername())){
            return ApiResult.fail(ErrorCode.PARAMS_ERROR.getCode(), ErrorCode.PARAMS_ERROR.getMsg());
        }
        /* 检查邮箱是否唯一 */
        User emailUser = findUserByEmail(user.getEmail());
        if (emailUser != null && !user.getId().equals(emailUser.getId())){
            return ApiResult.fail(ErrorCode.ACCOUNT_EXIST.getCode(),"该邮箱已经被注册了");
        }
        // 不能用于更新密码
        user.setPassword(null);
        userMapper.updateById(user);
        userVoRepository.save(copy(user));
        return ApiResult.success();
    }

    @Override
    public ApiResult insertUser(User user){
        /* 检查参数是否合法 */
        if (StringUtils.isBlank(user.getEmail())
                || StringUtils.isBlank(user.getUsername())
                || StringUtils.isBlank(user.getPassword())){
            return ApiResult.fail(ErrorCode.PARAMS_ERROR.getCode(), ErrorCode.PARAMS_ERROR.getMsg());
        }
        /* 检查邮箱是否唯一 */
        if (findUserByEmail(user.getEmail()) != null){
            return ApiResult.fail(ErrorCode.ACCOUNT_EXIST.getCode(),"该邮箱已经被注册了");
        }
        user.setPassword(loginService.encryptedPassword(user.getPassword()));
        user.setImage("");
        user.setStatus(1);
        saveUser(user);
        return ApiResult.success();
    }
    @Override
    public void deleteUserById(Long id) {
        userMapper.deleteById(id);
    }

    public UserVo copy(User user){
        UserVo userVo = new UserVo();
        BeanUtils.copyProperties(user, userVo);
        if (user.getCreateTime() != null ){
            userVo.setCreateTime(DateUtils.getTime(user.getCreateTime()));
        }
        return userVo;
    }
}
