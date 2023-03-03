package com.lsykk.caselibrary.dao.mapper;

import com.lsykk.caselibrary.dao.pojo.CaseHeader;
import com.lsykk.caselibrary.vo.params.PageParams;
import org.elasticsearch.common.inject.Inject;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class CaseHeaderMapperTest {
    @Mock
    private CaseHeaderMapper caseHeaderMapper;
    @Test
    public void insertAndGetIdTest() {
        CaseHeader caseHeader = new CaseHeader();
        caseHeader.setTitle("");
        caseHeader.setAuthorId(1L);
        caseHeader.setState(0);
        caseHeader.setVisible(1);
        caseHeader.setStatus(1);
        //boolean re = caseHeaderMapper.insertAndGetId(caseHeader);
        int re = caseHeaderMapper.insert(caseHeader);
        //Assert.assertEquals(1, re);
        //Assert.assertEquals(null, caseHeader);
    }
    @Test
    public void findCasesByFavoritesIdTest(){
    }
}
