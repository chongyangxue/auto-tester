package com.sohu.sce.autotest.utils;

public interface Constants {

	interface URL{
		String OPEN_API = "openapi.url";
		String CLOUDSCAPE = "cloudscape.url";
		String CONSOLE = "console.url";
		String REFERER = "referer.url";
		String ADMIN_URL = "admin.url";
	}
	
	interface STATUS_CODE {
		int SC_OK = 1000;
		int SC_TOKEN_UNAUTHORIZED = 1001;
		int SC_USER_APP_UNAUTHORIZED = 1002;
		int SC_SERVER_ERROR = 10500;
		int SC_RESOURCE_FORBIDDEN = 10403;
		int SC_RESOURCE_EXISTS = 10405;
		int SC_RESOURCE_NOT_EXISTS = 10404;
		int SC_PARAMETER_ERROR = 10406;

		// 101xx 为内部接口返回
		int SC_PROVIDER_ADD_ERROR = 10101;
		int SC_PROVIDER_DELETE_ERROR = 10102;

		int SC_SERVICE_INS_ADD_ERROR = 10601;
		int SC_SERVICE_INS_DELETE_ERROR = 10602;
		int SC_SERVICE_INS_BINDS_ADD_ERROR = 10603;
		int SC_SERVICE_INS_NOT_FOUND = 10604;
		int SC_SERVICE_INS_CREDENTIAL_EMPTY = 10605;
		int SC_SERVICE_INS_CACHE_ERROR = 10606;
		int SC_SERVICE_INS_CONFIG_ERROR = 10607;
		int SC_SERVICE_INS_CONFIG_LIMIT_ERROR = 10608;
		int SC_SERVICE_INS_BINDS_EXISTS = 10609;
		int SC_DEPOSIT_FAIL = 10620;
		int SC_ORDERID_EXISTS = 10621;
		int SC_ORDERID_USER_INVALID = 10622;
		int SC_ACCOUNT_SET_MINQUOTA_FAIL = 10623;

		int SC_APP_QUOTA_FULL = 10703;
		int SC_APP_NAME_FIRBIDDEN = 10704;
		int SC_APP_NAME_EXISTS = 10705;
		int SC_APP_FILE_NOT_FOUND = 10706;
		int SC_APP_RULENAME_EXISTS = 10707;
		int SC_APP_CONTAINER_TYPE_LIMIT = 10708;
		int SC_SSH_OPEN_FAIL = 10709;
		int SC_SSH_CLOSE_FAIL = 10710;
		int SC_SSH_OPEN_EXISTS = 10711;
		int SC_SSH_OPEN_NOT_EXISTS = 10712;
		int SC_APP_UA_EXISTS = 10713;
		int SC_APP_IP_EXISTS = 10714;
		int SC_APP_RATE_EXISTS = 10715;
		int SC_APP_INS_EMPTY = 10716;
		int SC_SSH_OPEN_LIMIT = 10717;

		int SC_PASSWORD_ERROR = 10653;
		int SC_AUTH_SELF_ERROR = 10656;
		int SC_USER_NOT_EXIST = 10657;
		int SC_USER_ROLE_EXIST = 10658;
		int SC_CAPTCHA_GET_ERROR = 10659;
		int SC_CAPTCHA_SEND_ERROR = 10660;
		int SC_CAPTCHA_INVALID = 10661;
		int SC_CAPTCHA_VERIRY_FAIL = 10662;
		int SC_CAPTCHA_TIME_60 = 10665;
		int SC_ACCOUNT_FROZEN = 10666;
		int SC_ACCOUNT_MONEY_MINUS = 10667;
		int SC_USER_EMAIL_UNBIND = 10668;
		int SC_USER_IDCARD_UNAUTH = 10669;

		int SC_INVITATION_APPLY_ERROR = 10670;
		int SC_INVITATION_ACTIVE_ERROR = 10671;
		int SC_INVITATION_LEFT_FINISH = 10672;
		int SC_INVITATION_NOT_FOUND = 10673;
		int SC_INVITATION_EXISTS = 10675;
		int SC_USER_MOBILE_UNBIND = 10674;
		int SC_USER_ACITVE_ALREADY = 10676;
		int SC_INVITATION_NOT_FOUND_OR_EXPIRED = 10677;
		int SC_INVITATION_BIND_ALREADY = 10678;
		int SC_USER_NOT_APPLY = 10679;
		int SC_USER_APPLY_ALREADY = 10684;
		int SC_RECEIPT_APPLY_ERROR = 10680;
		int SC_RECEIPT_QUERY_ERROR = 10681;
		int SC_INVITATION_INVALID = 10682;
		int SC_PROJECT_NOE_FOUND = 10683;
		int SC_GIT_DEPLOY_FAIL = 10685;

		int SC_EBS_ALIAS_EXISTS = 10730;
		int SC_EBS_APPLY_FAIL = 10731;

	}
	
	interface CONSOLE_STATUS{
		int OK = 1200;
	}

	interface REST {
		int DEFAULT_TIMEOUT = 10000;
	}

	interface ZK {
		String MASTER_ROOT = "/cluster/master";

		// 定义master
		String APP_MASTER_ROOT = MASTER_ROOT + "/appmaster";
		String CRON_MASTER_ROOT = MASTER_ROOT + "/cronmaster";
		String STAT_MASTER_ROOT = MASTER_ROOT + "/statmaster";

		// 定义master leader
		String APP_MASTER_LEADER = APP_MASTER_ROOT + "/leader";
		String CRON_MASTER_LEADER = APP_MASTER_ROOT + "/cronmaster";
		String STAT_MASTER_LEADER = APP_MASTER_ROOT + "/statmaster";
		
		String ZK_ROOT = "sce.zk.root";
		String ZK_TIMEOUT = "sce.zk.timeout";
		String ZK_AUTH = "sce.zk.auth";
		String ZK_URL = "sce.zk.url";
		
		//存储测试数据的路径
		String TEST_DATA = "/conf/modules/autotest/data.properties";

		interface SYSTEM {
			String PRIKEY = "pri.key";
			
			String REDIS_KEY = "redis.key";
			String REDIS_UID = "redis.uid";
			String REDIS_API = "redis.api";
		}

		interface MAIL {
			String CONF_MAIL = "/conf/modules/global/mail";
			String SENDCLOUD = "sendcloud";
			String HOST = "host";
			String PORT = "port";
			String USERNAME = "username";
			String PASSWORD = "password";
			String ADMIN = "admin";
			String ADMIN_RECEIVE = "admin.receive";
		}
		
		interface SMS {
			String URL = "sms.url";
			String WL_APPID = "sms.wl_appid";
			String SCE_APPID = "sms.sce_appid";
			String SECRET = "sms.secret";
			String KEY = "sms.key";
			String PRIORITY = "sms.priority";
			String LINKID = "sms.linkid";
			String TAILSP = "sms.tailsp";
			String TIMEOUT = "sms.timeout";
			String CAPTCHA_TIMEOUT = "sms.captcha.timeout";
			String CAPTCHA_ERRORTIMES = "sms.captcha.errortimes";
		}
		
	}
}
