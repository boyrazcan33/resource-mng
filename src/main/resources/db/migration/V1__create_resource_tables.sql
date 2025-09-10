-- Create resources table
CREATE TABLE resources (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    type VARCHAR(50) NOT NULL,
    country_code VARCHAR(2) NOT NULL,
    street_address VARCHAR(255) NOT NULL,
    city VARCHAR(100) NOT NULL,
    postal_code VARCHAR(20) NOT NULL,
    location_country_code VARCHAR(2) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    version BIGINT DEFAULT 0
);

-- Create characteristics table
CREATE TABLE characteristics (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    resource_id UUID NOT NULL,
    code VARCHAR(5) NOT NULL,
    type VARCHAR(50) NOT NULL,
    value VARCHAR(255) NOT NULL,
    CONSTRAINT fk_resource
        FOREIGN KEY (resource_id)
        REFERENCES resources(id)
        ON DELETE CASCADE
);

-- Create unique constraint for resource_id + code + type combination
ALTER TABLE characteristics
ADD CONSTRAINT uk_resource_code_type
UNIQUE (resource_id, code, type);

-- Create indexes for better query performance
CREATE INDEX idx_resources_country_code ON resources(country_code);
CREATE INDEX idx_resources_type ON resources(type);
CREATE INDEX idx_resources_created_at ON resources(created_at);
CREATE INDEX idx_characteristics_resource_id ON characteristics(resource_id);
CREATE INDEX idx_characteristics_type ON characteristics(type);

-- Create trigger to update updated_at timestamp
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

CREATE TRIGGER update_resources_updated_at
    BEFORE UPDATE ON resources
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();