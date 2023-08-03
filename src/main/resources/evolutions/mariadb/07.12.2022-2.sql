ALTER TABLE dockovpn.user_sessions MODIFY COLUMN session_id UUID NOT NULL;

ALTER TABLE dockovpn.user_sessions ADD CONSTRAINT user_sessions_UN UNIQUE KEY (session_id);
