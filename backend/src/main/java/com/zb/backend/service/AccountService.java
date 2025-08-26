package com.zb.backend.service;

import com.zb.backend.constants.enums.AccountEnum;
import com.zb.backend.constants.enums.ResultEnum;
import com.zb.backend.entity.Account;
import com.zb.backend.mapper.AccountMapper;
import com.zb.backend.model.JwtClaim;
import com.zb.backend.model.request.ChangePasswordRequest;
import com.zb.backend.model.response.AccountDetailResponse;
import com.zb.backend.model.response.LoginResponse;
import com.zb.backend.util.IdCardUtil;
import com.zb.backend.util.security.crypto.password.PasswordEncoder;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountService {
    private final AccountMapper accountMapper;
    private final PasswordEncoder passwordEncoder;

    // 根据id查询账号信心
    public Account getAccountByAccountId(Long accountId) {
        System.out.println("根据账号查询账号信息：" + accountId);
        return accountMapper.selectAccountByAccountId(accountId);
    }

    // 获取账号登录信息，判断是否是管理员调用不同的方法
    public LoginResponse getAccountLoginInfo(Long accountId, Boolean isAdmin) {
        System.out.println("获取登录信息账号：" + accountId);
        if (isAdmin) {
            return accountMapper.selectAdminLoginInfoByAccountId(accountId);
        }
        return accountMapper.selectEmpLoginInfoByAccountId(accountId);
    }

    // 更新最后登录时间
    public Boolean updateLastLoginTimeByAccountId(Long accountId) {
        System.out.println("更新最后登录时间：" + accountId);
        return accountMapper.updateLastLoginTime(accountId);
    }

    // 通过id判断是否为管理员
    public Boolean isAdminByAccountId(Long accountId) {
        return accountMapper.isAdminByAccountId(accountId);
    }

    // 新增账号
    public Boolean addAccount(String password) {
        return accountMapper.insertAccount(password);
    }

    // 修改账号状态
    public ResultEnum toggleAccountStatus(Long accountId) {
        Boolean updateAccountStatus = accountMapper.updateAccountStatus(accountId);

        if (!updateAccountStatus) {
            return AccountEnum.ERR_UPDATE_STATUS;
        }

        return AccountEnum.SUC_UPDATE_STATUS;
    }

    // 重置密码
    public ResultEnum resetPassword(Long accountId) {
        // 初始密码为123456，进行加密，调用修改密码方法
        String password = "123456";

        password = passwordEncoder.encode(password);

        Boolean resetPassword = accountMapper.updatePasswordByAccountId(accountId, password);
        if (!resetPassword) {
            return AccountEnum.ERR_RESET_PASSWORD;
        }

        return AccountEnum.SUC_RESET_PASSWORD;
    }

    // 修改密码
    public ResultEnum changePassword(Long accountId, @Valid ChangePasswordRequest changePasswordRequest) {
        /*
        * 通过accountId获取账号信息，对比原密码，是否正确
        * 判断新密码和确认密码是否相同
        * 判断完成后修改密码
        * 修改完成后，调用退出登录接口
        *  */

        Account account = accountMapper.selectAccountByAccountId(accountId);
        // 判断旧密码是否和数据库中相同
        if (!passwordEncoder.matches(changePasswordRequest.getOldPassword(), account.getPassword())) {
            return AccountEnum.ERR_OLD_PASSWORD;
        }

        // 判断新密码和确认密码是否相同
        if (!changePasswordRequest.getNewPassword().equals(changePasswordRequest.getConfirmPassword())) {
            return AccountEnum.ERR_MATCH_PASSWORD;
        }

        // 如果不存在上述错误，开始修改密码
        // 将新密码加密后传入Mapper
        String newPassword = passwordEncoder.encode(changePasswordRequest.getNewPassword());

        Boolean change = accountMapper.updatePasswordByAccountId(accountId, newPassword);

        if (!change) {
            return AccountEnum.ERR_CHANGE_PASSWORD;
        }

        return AccountEnum.SUC_CHANGE_PASSWORD;
    }

    // 获取个人信息详情
    public AccountDetailResponse getAccountDetail(JwtClaim jwtClaim) {
        // 判断当前账号是管理员还是员工
        if (jwtClaim.getIsAdmin()) {
            // 当前账号为管理员，调用查询管理员账号详情方法
            return accountMapper.selectAdminDetailByAccountId(jwtClaim.getAccountId());
        }
        AccountDetailResponse EmpDetail = accountMapper.selectEmpDetailAccountId(jwtClaim.getAccountId());
        // 处理身份证信息，将身份证信息解析为：年龄、性别、出生日期，并将身份证号设置为null
        String idCard = EmpDetail.getIdCard();
        EmpDetail.setAge(IdCardUtil.calculateAge(idCard));
        EmpDetail.setGender(IdCardUtil.parseGender(idCard));
        EmpDetail.setFormattedBirthday(IdCardUtil.parseBirthdayWithYear(idCard));
        EmpDetail.setIdCard(null);

        return EmpDetail;
    }
}
