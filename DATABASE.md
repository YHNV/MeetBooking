## 数据库设计

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



### Token 表（tokens）结构设计

#### 表基本信息

- **表名**：tokens
- **表注释**：Token 存储表，存储系统用户的 Token 信息及有效期

#### 字段详情

| 字段名        | 数据类型     | 约束 / 默认值              | 注释说明                           |
| ------------- | ------------ | -------------------------- | ---------------------------------- |
| `id`          | INT          | AUTO_INCREMENT PRIMARY KEY | 自增主键                           |
| `token`       | VARCHAR(255) | NOT NULL UNIQUE            | Token 字段，非空且唯一             |
| `account_id`  | BIGINT       | NOT NULL                   | Token 所属的`account_id`           |
| `expiry_time` | TIMESTAMP    | NOT NULL                   | Token 过期时间 为创建时间 + 2 小时 |

#### 设计说明

1. **主键设计**：使用`id`作为自增主键，确保记录唯一性
2. **关联关系**：`account_id`标识 Token 所属用户
3. **Token 特性**：`token`字段设置为非空且唯一，保证 Token 的有效性和唯一性
4. **有效期管理**：`expiry_time`字段用于控制 Token 有效期，支持身份验证的时效性控制

