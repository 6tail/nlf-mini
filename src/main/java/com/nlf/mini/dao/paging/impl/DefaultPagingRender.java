package com.nlf.mini.dao.paging.impl;

import com.nlf.mini.dao.paging.IPageable;
import com.nlf.mini.dao.paging.IPagingRender;
import com.nlf.mini.serialize.json.JSON;

/**
 * 默认分页渲染器
 *
 * @author 6tail
 */
public class DefaultPagingRender implements IPagingRender {
  public String render(IPageable pageable) {
    return JSON.fromObject(pageable);
  }

  public boolean support() {
    return true;
  }
}
