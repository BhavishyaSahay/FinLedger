CREATE TABLE users (
    id              BIGSERIAL PRIMARY KEY,
    email           TEXT NOT NULL UNIQUE,
    name            TEXT NOT NULL,
    password_hash   TEXT NOT NULL,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE expense_groups (
    id          BIGSERIAL PRIMARY KEY,
    name        TEXT NOT NULL,
    created_by  BIGINT NOT NULL REFERENCES users(id),
    created_at  TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE group_members (
    group_id    BIGINT NOT NULL REFERENCES expense_groups(id),
    user_id     BIGINT NOT NULL REFERENCES users(id),
    role        TEXT NOT NULL DEFAULT 'MEMBER' CHECK (role IN ('ADMIN', 'MEMBER')),
    joined_at   TIMESTAMPTZ NOT NULL DEFAULT now(),
    PRIMARY KEY (group_id, user_id)
);

CREATE TABLE expenses (
    id              BIGSERIAL PRIMARY KEY,
    group_id        BIGINT NOT NULL REFERENCES expense_groups(id),
    paid_by         BIGINT NOT NULL REFERENCES users(id),
    created_by      BIGINT NOT NULL REFERENCES users(id),
    description     TEXT NOT NULL,
    amount          NUMERIC(12, 2) NOT NULL CHECK (amount > 0),
    status          TEXT NOT NULL DEFAULT 'OPEN' CHECK (status IN ('OPEN', 'SETTLED', 'CLOSED')),
    split_type      TEXT NOT NULL CHECK (split_type IN ('EQUAL', 'EXACT')),
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE expense_splits (
    expense_id      BIGINT NOT NULL REFERENCES expenses(id) ON DELETE CASCADE,
    user_id         BIGINT NOT NULL REFERENCES users(id),
    amount_owed     NUMERIC(12, 2) NOT NULL CHECK (amount_owed > 0),
    status          TEXT NOT NULL DEFAULT 'PENDING' CHECK (status IN ('PENDING', 'PAID')),
    paid_via        TEXT CHECK (paid_via IN ('SELF', 'RAZORPAY', 'CASH')),
    paid_at         TIMESTAMPTZ,
    note            TEXT,
    PRIMARY KEY (expense_id, user_id),
    CHECK ((status = 'PAID') = (paid_at IS NOT NULL AND paid_via IS NOT NULL))
);

CREATE TABLE payments (
      id                  BIGSERIAL PRIMARY KEY,
      expense_id          BIGINT NOT NULL,
      user_id             BIGINT NOT NULL,              -- the person paying
      razorpay_order_id   TEXT NOT NULL UNIQUE,
      razorpay_payment_id TEXT UNIQUE,
      amount              NUMERIC(12, 2) NOT NULL CHECK (amount > 0),
      status              TEXT NOT NULL DEFAULT 'CREATED' CHECK (status IN ('CREATED', 'CAPTURED', 'FAILED')),
      created_at          TIMESTAMPTZ NOT NULL DEFAULT now(),
      updated_at          TIMESTAMPTZ NOT NULL DEFAULT now(),
      FOREIGN KEY (expense_id, user_id) REFERENCES expense_splits(expense_id, user_id)
);

-- A share can be paid successfully only once
CREATE UNIQUE INDEX uq_payment_captured_per_split
    ON payments (expense_id, user_id) WHERE status = 'CAPTURED';

-- Indexes for the queries the app runs most often
CREATE INDEX idx_group_members_user ON group_members(user_id);
CREATE INDEX idx_expenses_group     ON expenses(group_id, created_at DESC);
CREATE INDEX idx_splits_user        ON expense_splits(user_id, status);
CREATE INDEX idx_payments_split     ON payments(expense_id, user_id);