package com.zb.backend.service;

import com.zb.backend.constants.enums.AnnouncementEnum;
import com.zb.backend.constants.enums.ResultEnum;
import com.zb.backend.entity.Announcement;
import com.zb.backend.mapper.AnnouncementMapper;
import com.zb.backend.model.JwtClaim;
import com.zb.backend.model.PageRequest;
import com.zb.backend.model.PageResult;
import com.zb.backend.model.request.AddAnnouncementRequest;
import com.zb.backend.util.PaginationValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AnnouncementService {
    private final AnnouncementMapper announcementMapper;

    // 新增公告
    public ResultEnum addAnnouncement(AddAnnouncementRequest request, JwtClaim jwtClaim) {
        // 将管理员id赋值给发送者id
        request.setSenderId(jwtClaim.getAccountId());
        // 执行插入操作
        Boolean addAnn = announcementMapper.insertAnn(request);
        if (!addAnn) return AnnouncementEnum.ERR_ADD_ANN;
        return AnnouncementEnum.SUC_ADD_ANN;
    }

    // 分页获取公告
    public PageResult<Announcement> getAnnouncement(PageRequest pageRequest) {
        // 获取总数
        Integer total = announcementMapper.countAnn();
        // 调用分页校验
        PageResult<Announcement> pageResult = PaginationValidator.validatePagination(pageRequest, total);
        if (pageResult != null) return pageResult;
        // 记录数不为0，根据筛选条件查询
        List<Announcement> announcementList = announcementMapper.selectAnnList(pageRequest);
        return new PageResult<>(total, pageRequest.getPageNum(), pageRequest.getPageSize(), announcementList);
    }

    // 修改公告
    public ResultEnum updateAnnouncement(Announcement announcement) {
        // 首先判断公告是否存在
        Announcement existsAnn = announcementMapper.selectById(announcement.getAnnouncementId());
        if (existsAnn == null) return AnnouncementEnum.ERR_EXISTS_ID;
        // 如果存在，直接修改
        Boolean updateAnn = announcementMapper.updateAnn(announcement);
        if (!updateAnn) return AnnouncementEnum.ERR_UPDATE_ANN;
        return AnnouncementEnum.SUC_UPDATE_ANN;
    }

    // 删除公告
    public ResultEnum deleteAnnouncement(Long announcementId) {
        // 首先判断公告是否存在
        Announcement existsAnn = announcementMapper.selectById(announcementId);
        if (existsAnn == null) return AnnouncementEnum.ERR_EXISTS_ID;
        // 存在公告直接删除
        Boolean updateAnn = announcementMapper.deleteAnn(announcementId);
        if (!updateAnn) return AnnouncementEnum.ERR_DELETE_ANN;
        return AnnouncementEnum.SUC_DELETE_ANN;
    }
}
