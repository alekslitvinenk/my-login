ALTER TABLE dockovpn.users
ADD COLUMN time_created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP;