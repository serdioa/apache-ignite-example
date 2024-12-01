--
-- Values for Ignite security.
--

INSERT INTO t_Right(Right_Id, Right_Name, Description) VALUES(1, 'ignite.join-as-server', 'Join Ignite cluster as a node');
INSERT INTO t_Right(Right_Id, Right_Name, Description) VALUES(2, 'ignite.cache-create:*', 'Create any Ignite cache');
INSERT INTO t_Right(Right_Id, Right_Name, Description) VALUES(3, 'ignite.cache-destroy:*', 'Destroy any Ignite cache');
INSERT INTO t_Right(Right_Id, Right_Name, Description) VALUES(4, 'ignite.cache-read:*', 'Read data from any Ignite cache');
INSERT INTO t_Right(Right_Id, Right_Name, Description) VALUES(5, 'ignite.cache-remove:*', 'Remove data from any Ignite cache');
INSERT INTO t_Right(Right_Id, Right_Name, Description) VALUES(6, 'ignite.cache-put:*', 'Put data in any Ignite cache');

INSERT INTO t_Role(Role_Id, Role_Name, Description) VALUES(1, 'ignite_node', 'Node in an Ignite cluster');
INSERT INTO t_Role(Role_Id, Role_Name, Description) VALUES(2, 'ignite_rw_client', 'Read/write Ignite client');
INSERT INTO t_Role(Role_Id, Role_Name, Description) VALUES(3, 'ignite_ro_client', 'Read-only Ignite client');

-- A node in an Ignite cluster has all permissions.
INSERT INTO t_Role_Right(Role_Id, Right_Id) VALUES(1, 1);
INSERT INTO t_Role_Right(Role_Id, Right_Id) VALUES(1, 2);
INSERT INTO t_Role_Right(Role_Id, Right_Id) VALUES(1, 3);
INSERT INTO t_Role_Right(Role_Id, Right_Id) VALUES(1, 4);
INSERT INTO t_Role_Right(Role_Id, Right_Id) VALUES(1, 5);
INSERT INTO t_Role_Right(Role_Id, Right_Id) VALUES(1, 6);

-- A read/write user may read, write and delete data.
INSERT INTO t_Role_Right(Role_Id, Right_Id) VALUES(2, 4);
INSERT INTO t_Role_Right(Role_Id, Right_Id) VALUES(2, 5);
INSERT INTO t_Role_Right(Role_Id, Right_Id) VALUES(2, 6);

-- A read-only user may read data.
INSERT INTO t_Role_Right(Role_Id, Right_Id) VALUES(3, 4);

-- User running a standard Ignite node.
INSERT INTO t_User(User_Id, Username, Password, Locked) VALUES(1, 'clusterNodeUser', '{noop}clusterNodeUserPassword', FALSE);
INSERT INTO t_User_Role(User_Id, Role_Id) VALUES(1, 1);

-- User running a fat client.
-- A fat client is just a node in an Ignite cluster that does not hold any data, so it shall have the role "ignite_node".
INSERT INTO t_User(User_Id, Username, Password, Locked) VALUES(2, 'cmdClientUser', '{noop}cmdClientUserPassword', FALSE);
INSERT INTO t_User_Role(User_Id, Role_Id) VALUES(2, 1);
INSERT INTO t_User(User_Id, Username, Password, Locked) VALUES(3, 'webClientUser', '{noop}webClientUserPassword', FALSE);
INSERT INTO t_User_Role(User_Id, Role_Id) VALUES(3, 1);

-- User running a thin client.
INSERT INTO t_User(User_Id, Username, Password, Locked) VALUES(4, 'thinCmdClientUser', '{noop}thinCmdClientUserPassword', FALSE);
INSERT INTO t_User_Role(User_Id, Role_Id) VALUES(4, 2);

INSERT INTO t_User(User_Id, Username, Password, Locked) VALUES(5, 'thinWebClientUser', '{noop}thinWebClientUserPassword', FALSE);
INSERT INTO t_User_Role(User_Id, Role_Id) VALUES(5, 2);

--
-- Other sample values.
--

INSERT INTO t_Right(Right_Id, Right_Name, Description) VALUES(11, 'user:read', 'Read users');
INSERT INTO t_Right(Right_Id, Right_Name, Description) VALUES(12, 'user:write', 'Write users');
INSERT INTO t_Right(Right_Id, Right_Name, Description) VALUES(13, 'role:read', 'Read roles');
INSERT INTO t_Right(Right_Id, Right_Name, Description) VALUES(14, 'role:write', 'Write roles');

INSERT INTO t_Role(Role_Id, Role_Name, Description) VALUES(11, 'read_only', 'Read-only');
INSERT INTO t_Role(Role_Id, Role_Name, Description) VALUES(12, 'user_manager', 'User manager');
INSERT INTO t_Role(Role_Id, Role_Name, Description) VALUES(13, 'role_manager', 'Role manager');

-- Read-only role may read users and roles.
INSERT INTO t_Role_Right(Role_Id, Right_Id) VALUES(11, 11);
INSERT INTO t_Role_Right(Role_Id, Right_Id) VALUES(11, 13);

-- User manager role may read and write users.
INSERT INTO t_Role_Right(Role_Id, Right_Id) VALUES(12, 11);
INSERT INTO t_Role_Right(Role_Id, Right_Id) VALUES(12, 12);

-- Role manager role may read and write roles.
INSERT INTO t_Role_Right(Role_Id, Right_Id) VALUES(13, 13);
INSERT INTO t_Role_Right(Role_Id, Right_Id) VALUES(13, 14);

-- Create sample users.
INSERT INTO t_User(User_Id, Username, Password, Password_Changed_On, Expire_On, Locked)
    VALUES(11, 'Alice', '{noop}password_1', {fn timestampadd(SQL_TSI_DAY, -1, CURRENT_DATE)}, DATE({fn timestampadd(SQL_TSI_YEAR, 3, CURRENT_DATE)}), FALSE);
INSERT INTO t_User(User_Id, Username, Password, Password_Changed_On, Expire_On, Locked)
    VALUES(12, 'Bob', '{noop}password_2', {fn timestampadd(SQL_TSI_DAY, -1, CURRENT_DATE)}, NULL, FALSE);
INSERT INTO t_User(User_Id, Username, Password, Password_Changed_On, Expire_On, Locked)
    VALUES(13, 'Clarence', '{noop}password_3', {fn timestampadd(SQL_TSI_YEAR, -3, CURRENT_DATE)}, DATE({fn timestampadd(SQL_TSI_MONTH, -6, CURRENT_DATE)}), FALSE);
INSERT INTO t_User(User_Id, Username, Password, Password_Changed_On, Expire_On, Locked)
    VALUES(14, 'Dan', '{noop}password_4', {fn timestampadd(SQL_TSI_DAY, -15, CURRENT_DATE)}, NULL, TRUE);
INSERT INTO t_User(User_Id, Username, Password, Password_Changed_On, Expire_On, Locked)
    VALUES(15, 'Eva', '{noop}password_5', {fn timestampadd(SQL_TSI_YEAR, -3, CURRENT_DATE)}, DATE({fn timestampadd(SQL_TSI_YEAR, 3, CURRENT_DATE)}), TRUE);

INSERT INTO t_User(User_Id, Username, Password, Locked) VALUES(21, 'user_1', '{noop}password_1', FALSE);
INSERT INTO t_User(User_Id, Username, Password, Locked) VALUES(22, 'user_2', '{noop}password_2', TRUE);
INSERT INTO t_User(User_Id, Username, Password, Locked) VALUES(23, 'user_3', '{noop}password_3', TRUE);
INSERT INTO t_User(User_Id, Username, Password, Locked) VALUES(24, 'user_4', '{noop}password_4', FALSE);
INSERT INTO t_User(User_Id, Username, Password, Locked) VALUES(25, 'user_5', '{noop}password_5', TRUE);
INSERT INTO t_User(User_Id, Username, Password, Locked) VALUES(26, 'user_6', '{noop}password_6', FALSE);
INSERT INTO t_User(User_Id, Username, Password, Locked) VALUES(27, 'user_7', '{noop}password_7', TRUE);
INSERT INTO t_User(User_Id, Username, Password, Locked) VALUES(28, 'user_8', '{noop}password_8', FALSE);
INSERT INTO t_User(User_Id, Username, Password, Locked) VALUES(29, 'user_9', '{noop}password_9', FALSE);
INSERT INTO t_User(User_Id, Username, Password, Locked) VALUES(30, 'user_10', '{noop}password_10', FALSE);
INSERT INTO t_User(User_Id, Username, Password, Locked) VALUES(31, 'user_11', '{noop}password_11', TRUE);
INSERT INTO t_User(User_Id, Username, Password, Locked) VALUES(32, 'user_12', '{noop}password_12', FALSE);
INSERT INTO t_User(User_Id, Username, Password, Locked) VALUES(33, 'user_13', '{noop}password_13', TRUE);
INSERT INTO t_User(User_Id, Username, Password, Locked) VALUES(34, 'user_14', '{noop}password_14', FALSE);
INSERT INTO t_User(User_Id, Username, Password, Locked) VALUES(35, 'user_15', '{noop}password_15', FALSE);
INSERT INTO t_User(User_Id, Username, Password, Locked) VALUES(36, 'user_16', '{noop}password_16', FALSE);
INSERT INTO t_User(User_Id, Username, Password, Locked) VALUES(37, 'user_17', '{noop}password_17', TRUE);
INSERT INTO t_User(User_Id, Username, Password, Locked) VALUES(38, 'user_18', '{noop}password_18', FALSE);
INSERT INTO t_User(User_Id, Username, Password, Locked) VALUES(39, 'user_19', '{noop}password_19', TRUE);
INSERT INTO t_User(User_Id, Username, Password, Locked) VALUES(40, 'user_20', '{noop}password_20', FALSE);
INSERT INTO t_User(User_Id, Username, Password, Locked) VALUES(41, 'user_21', '{noop}password_21', FALSE);
INSERT INTO t_User(User_Id, Username, Password, Locked) VALUES(42, 'user_22', '{noop}password_22', FALSE);
INSERT INTO t_User(User_Id, Username, Password, Locked) VALUES(43, 'user_23', '{noop}password_23', TRUE);
INSERT INTO t_User(User_Id, Username, Password, Locked) VALUES(44, 'user_24', '{noop}password_24', FALSE);
INSERT INTO t_User(User_Id, Username, Password, Locked) VALUES(45, 'user_25', '{noop}password_25', FALSE);
INSERT INTO t_User(User_Id, Username, Password, Locked) VALUES(46, 'user_26', '{noop}password_26', FALSE);
INSERT INTO t_User(User_Id, Username, Password, Locked) VALUES(47, 'user_27', '{noop}password_27', FALSE);
INSERT INTO t_User(User_Id, Username, Password, Locked) VALUES(48, 'user_28', '{noop}password_28', FALSE);
INSERT INTO t_User(User_Id, Username, Password, Locked) VALUES(49, 'user_29', '{noop}password_29', TRUE);
INSERT INTO t_User(User_Id, Username, Password, Locked) VALUES(50, 'user_30', '{noop}password_30', FALSE);
INSERT INTO t_User(User_Id, Username, Password, Locked) VALUES(51, 'user_31', '{noop}password_31', TRUE);
INSERT INTO t_User(User_Id, Username, Password, Locked) VALUES(52, 'user_32', '{noop}password_32', FALSE);
INSERT INTO t_User(User_Id, Username, Password, Locked) VALUES(53, 'user_33', '{noop}password_33', FALSE);
INSERT INTO t_User(User_Id, Username, Password, Locked) VALUES(54, 'user_34', '{noop}password_34', FALSE);
INSERT INTO t_User(User_Id, Username, Password, Locked) VALUES(55, 'user_35', '{noop}password_35', FALSE);
INSERT INTO t_User(User_Id, Username, Password, Locked) VALUES(56, 'user_36', '{noop}password_36', FALSE);
INSERT INTO t_User(User_Id, Username, Password, Locked) VALUES(57, 'user_37', '{noop}password_37', TRUE);
INSERT INTO t_User(User_Id, Username, Password, Locked) VALUES(58, 'user_38', '{noop}password_38', FALSE);
INSERT INTO t_User(User_Id, Username, Password, Locked) VALUES(59, 'user_39', '{noop}password_39', FALSE);
INSERT INTO t_User(User_Id, Username, Password, Locked) VALUES(60, 'user_40', '{noop}password_40', FALSE);
INSERT INTO t_User(User_Id, Username, Password, Locked) VALUES(61, 'user_41', '{noop}password_41', TRUE);
INSERT INTO t_User(User_Id, Username, Password, Locked) VALUES(62, 'user_42', '{noop}password_42', FALSE);
INSERT INTO t_User(User_Id, Username, Password, Locked) VALUES(63, 'user_43', '{noop}password_43', TRUE);
INSERT INTO t_User(User_Id, Username, Password, Locked) VALUES(64, 'user_44', '{noop}password_44', FALSE);
INSERT INTO t_User(User_Id, Username, Password, Locked) VALUES(65, 'user_45', '{noop}password_45', FALSE);
INSERT INTO t_User(User_Id, Username, Password, Locked) VALUES(66, 'user_46', '{noop}password_46', FALSE);
INSERT INTO t_User(User_Id, Username, Password, Locked) VALUES(67, 'user_47', '{noop}password_47', TRUE);
INSERT INTO t_User(User_Id, Username, Password, Locked) VALUES(68, 'user_48', '{noop}password_48', FALSE);
INSERT INTO t_User(User_Id, Username, Password, Locked) VALUES(69, 'user_49', '{noop}password_49', FALSE);
INSERT INTO t_User(User_Id, Username, Password, Locked) VALUES(70, 'user_50', '{noop}password_50', FALSE);

INSERT INTO t_User_Role(User_Id, Role_Id) VALUES(11, 11);
INSERT INTO t_User_Role(User_Id, Role_Id) VALUES(12, 11);
INSERT INTO t_User_Role(User_Id, Role_Id) VALUES(12, 12);
INSERT INTO t_User_Role(User_Id, Role_Id) VALUES(13, 11);
INSERT INTO t_User_Role(User_Id, Role_Id) VALUES(13, 12);
INSERT INTO t_User_Role(User_Id, Role_Id) VALUES(13, 13);
INSERT INTO t_User_Role(User_Id, Role_Id) VALUES(14, 11);
INSERT INTO t_User_Role(User_Id, Role_Id) VALUES(15, 11);
INSERT INTO t_User_Role(User_Id, Role_Id) VALUES(15, 12);
