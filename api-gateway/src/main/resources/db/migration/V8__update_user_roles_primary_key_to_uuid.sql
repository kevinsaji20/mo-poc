ALTER TABLE user_roles ALTER COLUMN id DROP DEFAULT;

ALTER TABLE user_roles ALTER COLUMN id TYPE UUID USING gen_random_uuid();

ALTER TABLE user_roles ALTER COLUMN id SET DEFAULT gen_random_uuid();