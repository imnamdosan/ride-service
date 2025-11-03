CREATE TABLE IF NOT EXISTS rides (
    id UUID PRIMARY KEY,
    rider_id VARCHAR(64) NOT NULL,
    pickup TEXT NOT NULL,
    destination TEXT NOT NULL,
    status VARCHAR(32) NOT NULL CHECK (status IN ('PENDING', 'ASSIGNED', 'IN_PROGRESS', 'COMPLETED', 'CANCELLED')),
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    version INTEGER NOT NULL DEFAULT 0
);

CREATE INDEX IF NOT EXISTS idx_rides_status ON rides(status)



