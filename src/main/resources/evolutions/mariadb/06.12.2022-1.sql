ALTER TABLE dockovpn.user_sessions ADD last_touched TIMESTAMP NOT NULL;
ALTER TABLE dockovpn.user_sessions ADD time_expires TIMESTAMP NOT NULL;
