package com.zb.backend.service;

import com.zb.backend.constants.enums.AccountEnum;
import com.zb.backend.constants.enums.ResultEnum;
import com.zb.backend.entity.Account;
import com.zb.backend.mapper.AccountMapper;
import com.zb.backend.model.request.ChangePassword;
import com.zb.backend.model.response.LoginResponse;
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

    // 获取员工登录信息，联表查询
    public LoginResponse getEmpLoginInfo(Long accountId) {
        System.out.println("获取登录信息员工：" + accountId);
        return accountMapper.getEmpLoginInfoByAccountId(accountId);
    }

    // 获取管理员登录信息
    public LoginResponse getAdminLoginInfo(Long accountId) {
        System.out.println("获取登录信息管理员：" + accountId);
        return accountMapper.getAdminLoginInfoByAccountId(accountId);
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
    public ResultEnum changePassword(Long accountId, @Valid ChangePassword changePassword) {
        /*
        * 通过accountId获取账号信息，对比原密码，是否正确
        * 判断新密码和确认密码是否相同
        * 判断完成后修改密码
        * 修改完成后，调用退出登录接口
        *  */

        Account account = accountMapper.selectAccountByAccountId(accountId);
        // 判断旧密码是否和数据库中相同
        if (!passwordEncoder.matches(changePassword.getOldPassword(), account.getPassword())) {
            return AccountEnum.ERR_OLD_PASSWORD;
        }

        // 判断新密码和确认密码是否相同
        if (!changePassword.getNewPassword().equals(changePassword.getConfirmPassword())) {
            return AccountEnum.ERR_MATCH_PASSWORD;
        }

        // 如果不存在上述错误，开始修改密码
        // 将新密码加密后传入Mapper
        String newPassword = passwordEncoder.encode(changePassword.getNewPassword());

        Boolean change = accountMapper.updatePasswordByAccountId(accountId, newPassword);

        if (!change) {
            return AccountEnum.ERR_CHANGE_PASSWORD;
        }

        return AccountEnum.SUC_CHANGE_PASSWORD;
    }
}
