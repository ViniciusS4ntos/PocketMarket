CREATE TABLE cards (
                       id UUID PRIMARY KEY,

                       owner_id UUID NOT NULL,

                       name VARCHAR(150) NOT NULL,
                       set_name VARCHAR(100),

                       rarity VARCHAR(50),
                       card_condition VARCHAR(20),

                       price NUMERIC(10,2) NOT NULL,
                       stock INTEGER DEFAULT 1,

                       image_url VARCHAR(500),
                       description TEXT,

                       deleted BOOLEAN DEFAULT FALSE,

                       CONSTRAINT fk_card_owner
                           FOREIGN KEY (owner_id)
                               REFERENCES users(id)
);