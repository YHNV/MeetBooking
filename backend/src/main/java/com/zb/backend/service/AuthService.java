package com.zb.backend.service;

import com.zb.backend.constants.enums.AuthEnum;
import com.zb.backend.constants.enums.ErrorEnum;
import com.zb.backend.constants.enums.ResultEnum;
import com.zb.backend.entity.Account;
import com.zb.backend.model.JwtClaim;
import com.zb.backend.model.request.RegisterRequest;
import com.zb.backend.model.response.LoginResponse;
import com.zb.backend.util.JwtUtil;
import com.zb.backend.util.security.crypto.password.PasswordEncoder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final AccountService accountService;
    private final DepartmentService departmentService;
    private final EmployeeService employeeService;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    // 登录
    public ResultEnum login(Long accountId, String password) {

        // 通过账号查询用户是否存在
        Account account = accountService.getAccountByAccountId(accountId);

        // 如果不存在
        if (account == null) {
            return AuthEnum.ERR_ACC_NOT_EXIST;   // 账号不存在
        }

        // 如果账号被冻结
        if (!account.getIsActive()) {
            return AuthEnum.ERR_ACC_LOCKED;   // 账号被冻结不可登录
        }

        // 获取密码
        String storePassword = account.getPassword();

        // 将用户输入的密码调用加密算法进行对比
        if (!passwordEncoder.matches(password, storePassword)) {
            // 登录失败
            return AuthEnum.ERR_WRONG_PWD;
        }

        // 密码校验成功，登录成功
        return AuthEnum.SUC_LOGIN;
    }

    // 登录获取用户信息
    public LoginResponse getLoginInfo(Long accountId) {
        /*
         * 账号表：
         * Boolean isAdmin;
         * Boolean firstLogin;
         * LocalDateTime lastLoginTime;
         *
         * 员工表：
         * Long empId;
         * String empName;
         * Boolean isManager;
         * String position;
         *
         * 部门表：
         * String deptName;
         *
         * 获取Token
         * String token;
         *
         *  */

        // 通过accountId获取账号信息，有accountService判断是否管理员
        Account account = accountService.getAccountByAccountId(accountId);

        LoginResponse loginResponse = accountService.getAccountLoginInfo(accountId, account.getIsAdmin());

        // 查询完成后，获取Token，传入LoginResponse.token
        String token = JwtUtil.createToken(new JwtClaim(accountId, loginResponse.getIsAdmin()));
        loginResponse.setToken(token);

        // 删除数据库中已过期的Token
        int deleteExpiredTokens = tokenService.deleteExpiredTokens();
        System.out.println("删除了" + deleteExpiredTokens + "条过期Token");

        // 删除当前登录id的Token
        int deleteTokenByAccountId = tokenService.deleteTokenByAccountId(accountId);
        System.out.println(accountId + "用户删除Token：" + deleteTokenByAccountId + "条");

        // 将生成的Token放入Token表中
        Boolean isToken = tokenService.insertToken(accountId, token);
        System.out.println("是否存入Token：" + isToken);

        // 获取完成后，更新最后登录时间，这样用户获取的就都是上次的登录时间
        Boolean isUpdate = accountService.updateLastLoginTimeByAccountId(accountId);
        System.out.println("最后登录时间是否更新成功：" + isUpdate);

        return loginResponse;
    }

    // 退出登录
    public ResultEnum logout(Long accountId) {
        System.out.println("需要退出的accountId：" + accountId);
        // 删除数据库中已过期的Token
        int deleteExpiredTokens = tokenService.deleteExpiredTokens();
        System.out.println("删除了" + deleteExpiredTokens + "条过期Token");

        // 如果能进到这个接口，说明Token一定有效，因为前面有拦截器拦截，过期Token过不来，所以只需要删除Token就可以了

        // 删除当前登录id的Token
        int deleteTokenByAccountId = tokenService.deleteTokenByAccountId(accountId);
        System.out.println(accountId + "用户删除Token：" + deleteTokenByAccountId + "条");

        if (deleteTokenByAccountId <= 0) {
            return AuthEnum.ERR_LOGOUT;
        }
        // 删除成功
        return AuthEnum.SUC_LOGOUT;
    }

    // 注册
    @Transactional(rollbackFor = Exception.class)
    public ResultEnum register(RegisterRequest registerRequest) {
        /*
         * 注册逻辑
         * 接收到注册请求模型，进行四个操作
         * 1.判断是否为管理员，使用accountId进行查询；已在拦截其中实现
         * 2.判断所添加的部门是否存在
         * 3.判断手机号是否存在
         * 4.判断身份证号是否存在
         *  */

        // 判断部门是否存在，如果不存在返回，抛出异常
        if (!departmentService.existsByDeptId(registerRequest.getDeptId())) {
            // throw new RuntimeException(AuthEnum.ERR_DEPT_NOT_EXIST.getMessage());
            return AuthEnum.ERR_DEPT_NOT_EXIST;
        }

        // 判断手机号是否存在，如果存在，抛出异常
        if (employeeService.existsPhone(registerRequest.getPhone())) {
            // throw new RuntimeException(AuthEnum.ERR_PHONE_DUPLICATE.getMessage());
            return AuthEnum.ERR_PHONE_DUPLICATE;
        }

        // 判断身份证号是否存在，如果存在，抛出异常
        if (employeeService.existsIdCard(registerRequest.getIdCard())) {
            // throw new RuntimeException(AuthEnum.ERR_IDCARD_DUPLICATE.getMessage());
            return AuthEnum.ERR_IDCARD_DUPLICATE;
        }

        // 都不存在，执行注册逻辑，需要Accounts和Employees一起执行
        // 新增Accounts，固定密码123456，并加密
        // 获取请求中的密码
        String password = registerRequest.getPassword();

        // 如果密码不存在（为空或空白字符串），则使用默认密码123456
        if (password == null || password.trim().isEmpty()) {
            password = "123456";
        }

        // 密码加密
        String encodedPassword = passwordEncoder.encode(password);

        // 执行注册（try catch包围，两条语句只要有一条执行，哪怕出错，第二条也必须执行）
        Boolean addAccount = false;
        Boolean addEmployee = false;

        try {
            addAccount = accountService.addAccount(encodedPassword);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            addEmployee = employeeService.addEmployee(registerRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 如果执行出现异常，手动抛出异常，否则不能回滚
        if (!addAccount || !addEmployee) {
            throw new RuntimeException(ErrorEnum.ERR_SERVER.getMessage());
            // return ErrorEnum.ERR_SERVER;
        }

        return AuthEnum.SUC_REGISTER;
    }
}
