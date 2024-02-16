CREATE TABLE IF NOT EXISTS users (
    id SERIAL4 PRIMARY KEY,
    "limit" BIGINT NOT NULL,
    amount BIGINT NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS transactions (
    id SERIAL4 PRIMARY KEY,
    user_id INT4 NOT NULL,
    amount INT8 NOT NULL,
    type CHAR(1) NOT NULL,
    description VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE OR REPLACE FUNCTION update_balance_and_insert_transaction(p_user_id INTEGER, p_amount_to_subtract INTEGER, p_type CHAR, p_desc VARCHAR(255))
RETURNS TABLE (updated_amount INTEGER) AS
$$
DECLARE
    user_limit INTEGER;
BEGIN
    IF p_type = 'd' THEN
        UPDATE users
        SET amount = amount - p_amount_to_subtract
        WHERE id =  p_user_id
        RETURNING amount INTO updated_amount;
    ELSIF p_type = 'c' THEN
        UPDATE users
        SET amount = amount + p_amount_to_subtract
        WHERE id = p_user_id
        RETURNING amount INTO updated_amount;
    ELSE
        RAISE EXCEPTION '422';
    END IF;

    IF updated_amount IS NULL then
            RAISE EXCEPTION '422';
    END IF;

    IF updated_amount IS NOT NULL THEN
        INSERT INTO public.transactions
        (user_id, amount, "type", description)
        VALUES (p_user_id, p_amount_to_subtract, p_type, p_desc);
    END IF;

    RETURN QUERY SELECT updated_amount;
END;
$$
LANGUAGE plpgsql;

INSERT INTO users ("limit") VALUES (100000);
INSERT INTO users ("limit") VALUES (80000);
INSERT INTO users ("limit") VALUES (1000000);
INSERT INTO users ("limit") VALUES (10000000);
INSERT INTO users ("limit") VALUES (500000);