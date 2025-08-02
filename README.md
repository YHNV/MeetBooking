# 系统设计

## 系统界面

### 公共界面

#### 登录页

账号+密码登录（输入框内提示请输入工号）
提示初始密码，首次登录需重置密码
未登录用户拦截到登录页

#### 页面布局

顶部导航栏设计
左侧logo
菜单居中
右侧功能区：消息通知按钮+用户信息（头像+用户名）
点击头像弹出下拉菜单：个人中心、退出登录

菜单栏和下方内容根据用户类型而变化



### 员工界面

#### 首页

左上：参与会议数量+消息数量（小卡片）
右上：未定
左下：已预约会议信息（列表）
右下：快速预约入口

#### 会议室列表

展示所有会议室，员工仅小型会议室可预约（卡片）
上方筛选工具
点机会议室可以查看详情（实拍图、位置、设备）

#### 会议室预约（弹窗）

会议主题、会议内容、参会人数
开始时间、结束时间（半小时一个时间段）
选择参与人（只显示同部门人员，可多选）
提交预约申请

#### 我的预约

查看已预约会议信息（列表+日历视图）
对预约进行查看和操作（待审核/已通过/已拒绝/已取消）
对未开始的会议进行取消
查看预约详情

#### 通知中心（右侧滑出）

分两个板块：个人和系统
个人：
会议开始提醒（前30分钟、前15分钟、开始时）
部门内邀请会议@通知
系统：
预约审核结果
管理员群发公告或单独提醒
对通知进行已读

#### 个人信息页

查看个人信息
修改个人信息（只可修改手机号、密码）无需审核
设置邮箱提醒（选做）



### 经理界面

继承员工界面所有功能，并增加如下内容：
会议室预约可选大型会议室
在预约-参与人员中，可选@部门所有员工

部门内会议室数据统计页面（选做）



### 管理员界面

#### 用户管理

查看所有员工信息
批量创建员工账号（Excel上传）
新增/编辑员工账号
启用/禁用员工账号
重置员工密码

#### 会议室管理

对会议室详情进行增删改查（类型、容量、设备、位置、图片）
管理会议室状态（可用/停用/维护）
会议室设备管理（可考虑单独开一个页面）

#### 预约审核

查看所有预约审核列表，并标注申请人类型（员工、经理）
同意/拒绝预约（填写拒绝原因）
查看预约审核历史记录

#### 会议室统计

展示全公司会议室使用率（每日/每月）

#### 通知

给全体员工发公告
给单个员工发信息



## API接口











## 数据库设计

### ID序列

```sql
DROP SEQUENCE IF EXISTS dept_seq;
DROP SEQUENCE IF EXISTS user_seq;

-- 部门 ID 序列
CREATE SEQUENCE dept_seq START WITH 10001;

-- 账号 ID 序列
CREATE SEQUENCE account_seq START WITH 10001;

-- 用户 ID 序列
CREATE SEQUENCE emp_seq START WITH 10001; -- 员工工号/员工账号序列，从10001开始
CREATE SEQUENCE admin_seq START WITH 101; -- 管理员账号序列，从101开始
```





### 部门表（departments）结构设计

#### 表基本信息

- **表名**：departments
- **表注释**：部门信息表，存储公司所有部门的基本信息及状态

#### 字段详情

| 字段名         | 数据类型     | 约束 / 默认值                                            | 注释说明                                             |
| -------------- | ------------ | -------------------------------------------------------- | ---------------------------------------------------- |
| `dept_id`      | BIGINT       | DEFAULT NEXTVAL('dept_seq')<br>PRIMARY KEY               | 部门 ID，主键<br>通过序列`dept_seq`自动生成唯一值    |
| `dept_name`    | VARCHAR(63)  | NOT NULL<br>UNIQUE                                       | 部门名称<br>非空且在表中唯一，不允许重复             |
| `manager_id`   | BIGINT       | 无默认值<br>可空                                         | 部门经理 ID<br>关联员工表（employees）的`emp_id`字段 |
| `dept_address` | VARCHAR(255) | 无默认值<br>可空                                         | 部门办公地址<br>例如："3号楼5层东侧"                 |
| `dept_desc`    | TEXT         | 无默认值<br>可空                                         | 部门详情描述<br>包括部门职责、人员规模等信息         |
| `is_active`    | BOOLEAN      | DEFAULT TRUE                                             | 部门启用状态<br>`TRUE`=启用，`FALSE`=禁用            |
| `create_time`  | TIMESTAMP    | DEFAULT CURRENT_TIMESTAMP                                | 部门创建时间<br>自动记录数据插入时间                 |
| `update_time`  | TIMESTAMP    | DEFAULT CURRENT_TIMESTAMP<br>ON UPDATE CURRENT_TIMESTAMP | 最后修改时间<br>数据更新时自动刷新时间戳             |

#### 设计说明

1. **主键设计**：使用`dept_id`作为主键，通过序列`dept_seq`自动生成，确保唯一性
2. **关联关系**：`manager_id`与员工表的`emp_id`形成外键关联
3. **时间跟踪**：通过`create_time`和`update_time`自动记录部门信息的创建和修改时间，便于数据审计
4. **状态管理**：`is_active`字段用于标记部门是否启用，支持逻辑删除而非物理删除
5. **数据约束**：`dept_name`设置为非空且唯一，避免部门名称重复或为空

#### 建表SQL语句

```sql
DROP TABLE IF EXISTS departments;

-- 创建部门表
CREATE TABLE departments
(
    dept_id      BIGINT    DEFAULT NEXTVAL('dept_seq') COMMENT '部门ID，主键',
    dept_name    VARCHAR(63) NOT NULL UNIQUE COMMENT '部门名称，非空，唯一',
    manager_id   BIGINT COMMENT '部门经理ID，关联员工表emp_id',
    dept_address VARCHAR(255) COMMENT '部门地址',
    dept_desc    TEXT COMMENT '部门详情',
    is_active    BOOLEAN   DEFAULT TRUE COMMENT '部门是否启用，默认true',
    create_time  TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time  TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    PRIMARY KEY (dept_id)
);

COMMENT ON TABLE departments IS '部门信息表';
```





### 账号表（accounts）结构设计

#### 表基本信息

- **表名**：accounts
- **表注释**：账号信息表，存储系统用户的登录账号及权限信息
- **核心作用**：管理用户登录凭证、权限控制及登录状态跟踪

#### 字段详情

| 字段名            | 数据类型     | 约束 / 默认值                                            | 注释说明                                                     |
| ----------------- | ------------ | -------------------------------------------------------- | ------------------------------------------------------------ |
| `account_id`      | BIGINT       | DEFAULT NEXTVAL('account_seq')<br>PRIMARY KEY            | 账号ID，主键<br>通过序列`account_seq`自动生成，与员工表emp_id关联 |
| `password`        | VARCHAR(127) | NOT NULL                                                 | 加密后的密码<br>存储经过加密处理的用户密码，非空             |
| `is_admin`        | BOOLEAN      | DEFAULT FALSE                                            | 是否为管理员<br>`TRUE`=管理员，`FALSE`=普通用户              |
| `is_active`       | BOOLEAN      | DEFAULT TRUE                                             | 账号是否启用<br>`TRUE`=启用，`FALSE`=禁用（锁定）            |
| `first_login`     | BOOLEAN      | DEFAULT TRUE                                             | 是否首次次登录<br>`TRUE`=未修改初始密码，`FALSE`=已修改密码  |
| `last_login_time` | TIMESTAMP    | 无默认值（可空）                                         | 最后登录时间<br>记录用户最近一次登录系统的时间               |
| `create_time`     | TIMESTAMP    | DEFAULT CURRENT CURRENT_TIMESTAMP                        | 创建时间<br>自动动记录账号创建时间                           |
| `update_time`     | TIMESTAMP    | DEFAULT CURRENT_TIMESTAMP<br>ON UPDATE CURRENT_TIMESTAMP | 最后修改时间<br>账号信息更新时自动刷新时间戳                 |

#### 设计说明

1. **主键设计**：使用`account_id`作为主键，通过序列`account_seq`自动生成，与员工表的`emp_id`一一对应，形成关联关系
2. **安全设计**：
    - 密码字段`password`使用VARCHAR(127)存储加密后的密码（支持多种加密算法结果）
    - `first_login`标记用户是否首次登录，用于强制初始密码修改
3. **关联关系**：`account_id`（当前表） ↔ `emp_id`（员工表）形成外键关联，一对一关联
4. **权限控制**：`is_admin`字段区分管理员和普通用户，实现基础权限隔离
5. **状态管理**：`is_active`字段用于账号启用/禁用控制，支持账号临时锁定功能
6. **审计跟踪**：通过`last_login_time`、`create_time`和`update_time`跟踪账号使用和修改记录

#### 建表SQL语句

```sql
DROP TABLE IF EXISTS accounts;

-- 创建账号表
-- 账号ID为员工工号，与员工表ID相关联
CREATE TABLE accounts
(
    account_id      BIGINT    DEFAULT NEXTVAL('account_seq') COMMENT '账号ID，主键',
    password        VARCHAR(127) NOT NULL COMMENT '加密后的密码，非空',
    is_admin        BOOLEAN   DEFAULT FALSE COMMENT '是否为管理员，默认false',
    is_active       BOOLEAN   DEFAULT TRUE COMMENT '账号是否启用，默认true',
    first_login     BOOLEAN   DEFAULT TRUE COMMENT '是否首次登录，默认true',
    last_login_time TIMESTAMP COMMENT '最后登录时间',
    create_time     TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time     TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    -- 设置主键
    PRIMARY KEY (account_id)
);

COMMENT ON TABLE accounts IS '账号信息表';
```



### 员工表（employees）结构设计

#### 表基本信息

- **表名**：employees
- **表注释**：员工信息表，存储企业员工的基本信息及组织归属
- **核心作用**：管理员工个人信息、部门归属及职位信息，与账号表和部门表形成关联

#### 字段详情

| 字段名        | 数据类型    | 约束 / 默认值                                                | 注释说明                                                     |
| ------------- | ----------- | ------------------------------------------------------------ | ------------------------------------------------------------ |
| `emp_id`      | BIGINT      | DEFAULT NEXTVAL('emp_seq')<br>PRIMARY KEY                    | 员工工号，主键<br>通过序列`emp_seq`自动生成，与账号表`account_id`关联 |
| `emp_name`    | VARCHAR(31) | NOT NULL                                                     | 员工姓名，非空                                               |
| `dept_id`     | BIGINT      | NOT NULL<br>FOREIGN KEY REFERENCES departments(dept_id) ON DELETE RESTRICT | 所属部门ID<br>关联部门表`dept_id`，部门存在员工时不允许删除  |
| `position`    | VARCHAR(31) | 无默认值（可空）                                             | 部门职位（如"开发工程师"、"部门经理"）                       |
| `phone`       | VARCHAR(15) | NOT NULL                                                     | 联系电话，非空                                               |
| `email`       | VARCHAR(63) | 无默认值（可空）                                             | 工作邮箱，用于系统通知                                       |
| `id_card`     | VARCHAR(18) | NOT NULL UNIQUE                                              | 身份证号，非空且唯一（用于身份验证）                         |
| `is_manager`  | BOOLEAN     | DEFAULT FALSE                                                | 是否为管理人员<br>`TRUE`=管理人员，`FALSE`=普通员工          |
| `create_time` | TIMESTAMP   | DEFAULT CURRENT_TIMESTAMP                                    | 创建时间<br>自动记录员工信息录入时间                         |
| `update_time` | TIMESTAMP   | DEFAULT CURRENT_TIMESTAMP<br>ON UPDATE CURRENT_TIMESTAMP     | 最后修改时间<br>员工信息更新时自动刷新时间戳                 |

#### 设计说明

1. **主键设计**：使用`emp_id`作为主键，通过序列`emp_seq`自动生成，同时作为外键关联账号表的`account_id`，实现"一人一账号"的映射关系
2. **关联关系**：
    - 与部门表通过`dept_id`关联，使用`ON DELETE RESTRICT`确保部门有员工时无法删除
    - 与账号表通过`emp_id`和`account_id`一对一关联，确保员工账号的唯一性
3. **数据约束**：
    - 核心字段（姓名、电话、身份证号）设置为非空，保证基础信息完整
    - 身份证号设置唯一约束，避免重复录入
4. **管理标识**：`is_manager`字段用于标记管理人员，支持组织架构的层级管理

#### 建表SQL语句

```sql
DROP TABLE IF EXISTS employees;

-- 创建员工信息表
-- 员工ID和账号ID相关联，账号表为主表
-- 员工表dept_id关联部门表dept_id
CREATE TABLE employees
(
    emp_id      BIGINT    DEFAULT NEXTVAL('emp_seq') COMMENT '员工工号，主键',
    emp_name    VARCHAR(31) NOT NULL COMMENT '员工姓名，非空',
    dept_id     BIGINT      NOT NULL COMMENT '所属部门ID，关联部门表dept_id，非空',
    position    VARCHAR(31) COMMENT '部门职位，可空',
    phone       VARCHAR(15) NOT NULL COMMENT '联系电话，非空',
    email       VARCHAR(63) COMMENT '提醒邮箱，可空',
    id_card     VARCHAR(18) NOT NULL UNIQUE COMMENT '身份证号，非空唯一',
    is_manager  BOOLEAN   DEFAULT FALSE COMMENT '是否为经理，默认false',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    -- 设置主键
    PRIMARY KEY (emp_id),
    -- 外键约束
    -- 当部门内还有员工时，无法删除该部门
    FOREIGN KEY (dept_id) REFERENCES departments (dept_id) ON DELETE RESTRICT,
    -- 员工工号关联账号
    FOREIGN KEY (emp_id) REFERENCES accounts (account_id)
);

COMMENT ON TABLE employees IS '员工信息表';
```















