CREATE TABLE user_roles (
    id BIGSERIAL PRIMARY KEY,

    user_id UUID NOT NULL,
    role_id UUID NOT NULL,

    assigned_by UUID,
    assigned_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),

    expires_at TIMESTAMP WITH TIME ZONE,

    CONSTRAINT fk_user_roles_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_user_roles_role FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE,

    CONSTRAINT fk_user_roles_assigned_by FOREIGN KEY (assigned_by) REFERENCES users(id) ON DELETE SET NULL,

    CONSTRAINT uq_user_role UNIQUE(user_id, role_id)
);