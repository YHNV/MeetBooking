# API设计



## 基础信息

- **基础访问路径**: `http://localhost:8080`

- **API 规范**: OpenAPI 3.1.0

- **所有请求响应都为JSON格式**

- **所有请求需要带上Token**

- **通用响应结构 (Result)**

  ```json
  {
    "code": "integer, 状态码",
    "msg": "string, 响应消息",
    "data": "object, 响应数据(可能为null)"
  }
  ```



## 认证授权 (Auth Controller)

### 1. 注册

- **请求路径**：`/auth/register`
- **请求方法**：POST
- **请求权限**：管理员
- **请求参数**：RegisterRequest

| 字段名    | 类型    | 必填 | 描述         | 约束                                  |
| :-------- | :------ | :--- | :----------- | :------------------------------------ |
| empName   | string  | 是   | 员工姓名     | 长度: 0-31                            |
| deptId    | long    | 是   | 部门ID       |                                       |
| phone     | string  | 是   | 手机号       | 必须符合中国手机号格式: ^1[3-9]\d{9}$ |
| idCard    | string  | 是   | 身份证号     | 必须符合中国身份证格式                |
| password  | string  | 否   | 密码         | 用户无需输入，默认123456              |
| position  | string  | 否   | 职位         | 长度: 0-31                            |
| email     | string  | 否   | 邮箱         | 长度: 0-63                            |
| isManager | boolean | 否   | 是否为管理员 |                                       |

- **响应类别**：

| code | message                | data    | 备注                 |
| :--- | :--------------------- | :------ | :------------------- |
| 2004 | "注册成功"             | empName | SUC_REGISTER         |
| 4007 | "电话号码已被占用"     | -       | ERR_PHONE_DUPLICATE  |
| 4008 | "身份证号码已被占用"   | -       | ERR_IDCARD_DUPLICATE |
| 4009 | "部门ID不存在"         | -       | ERR_DEPT_NOT_EXIST   |
| 4010 | "无权限，无法执行操作" | -       | ERR_NO_PERMISSION    |

- **响应参数**：

| 字段名  | 类型   | 描述     |
| :------ | :----- | :------- |
| empName | string | 员工姓名 |



### 2. 登录

- **请求路径**：`/auth/login`
- **请求方法**：POST
- **请求权限**：所有人
- **请求参数**：LoginRequest

| 字段名    | 类型   | 必填 | 描述   |
| :-------- | :----- | :--- | :----- |
| accountId | long   | 是   | 账号ID |
| password  | string | 是   | 密码   |

- **响应类别**：

| code | message                  | data         | 备注（枚举常量名） |
| :--- | :----------------------- | :----------- | :----------------- |
| 2001 | "登录成功"               | -            | SUC_LOGIN          |
| 2002 | "获取用户信息成功"       | LoginRequest | SUC_INFO           |
| 4001 | "密码错误"               | -            | ERR_WRONG_PWD      |
| 4002 | "账号已被冻结，不可登录" | -            | ERR_ACC_LOCKED     |
| 4003 | "账号不存在"             | -            | ERR_ACC_NOT_EXIST  |
| 4004 | "用户未登录或令牌无效"   | -            | ERR_NO_LOGIN       |
| 4005 | "用户信息不存在"         | -            | ERR_INFO_NOT_EXIST |

- **响应参数**：LoginRequest

| 字段名        | 类型          | 描述           |
| :------------ | :------------ | :------------- |
| accountId     | long          | 账号ID         |
| isAdmin       | boolean       | 是否为管理员   |
| isManager     | boolean       | 是否为部门经理 |
| firstLogin    | boolean       | 是否首次登录   |
| lastLoginTime | localdatetime | 上次登录时间   |
| deptName      | string        | 部门名称       |
| position      | string        | 职位           |
| empId         | long          | 员工ID         |
| empName       | string        | 员工姓名       |
| token         | string        | 认证令牌       |



### 3. 退出登录

- **请求路径**：`/auth/logout`
- **请求方法**：POST
- **请求权限**：所有人
- **请求参数**：accountId

| 字段名    | 类型 | 必填 | 描述   |
| :-------- | :--- | :--- | :----- |
| accountId | long | 是   | 用户ID |

- **响应类别**：

| code | message                | data | 备注（）   |
| :--- | :--------------------- | :--- | :--------- |
| 2003 | "退出登录成功"         | -    | SUC_LOGOUT |
| 4006 | "令牌失效，请重新登录" | -    | ERR_LOGOUT |

- **响应参数**：accountId

| 字段名    | 类型 | 描述   |
| :-------- | :--- | :----- |
| accountId | long | 用户ID |

