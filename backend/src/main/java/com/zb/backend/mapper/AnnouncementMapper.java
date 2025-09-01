package com.zb.backend.mapper;

import com.zb.backend.entity.Announcement;
import com.zb.backend.model.PageRequest;
import com.zb.backend.model.request.AddAnnouncementRequest;

import java.util.List;

public interface AnnouncementMapper {
    // 添加公告
    Boolean insertAnn(AddAnnouncementRequest request);

    // 获取公告总数
    Integer countAnn();

    // 分页获取公告
    List<Announcement> selectAnnList(PageRequest pageRequest);

    // 根据公告id查询公告
    Announcement selectById(Long announcementId);

    // 修改公告
    Boolean updateAnn(Announcement announcement);

    // 删除公告
    Boolean deleteAnn(Long announcementId);
}
