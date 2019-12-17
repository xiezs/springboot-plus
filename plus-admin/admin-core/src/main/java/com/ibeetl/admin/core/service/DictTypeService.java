package com.ibeetl.admin.core.service;

import com.ibeetl.admin.core.dao.CoreDictDao;
import com.ibeetl.admin.core.entity.DictType;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class DictTypeService {

  @Autowired private CoreDictDao dictDao;

  public List<DictType> findAllList(String type) {
    return dictDao.findAllDictType(type);
  }
}
