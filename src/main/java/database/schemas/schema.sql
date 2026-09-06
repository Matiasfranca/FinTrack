PRAGMA foreign_keys = ON;

CREATE TABLE IF NOT EXISTS MONTH (
    id INTEGER PRIMARY KEY,
    year INTEGER NOT NULL,
    month INTEGER NOT NULL,
    
    UNIQUE (year, month)
);


CREATE TABLE IF NOT EXISTS BANK_ACCOUNT (
    id INTEGER PRIMARY KEY,
    name TEXT NOT NULL,
    type TEXT NOT NULL CHECK (
        type IN (
            'CHECKING',
            'SAVINGS',
            'CASH',
            'OTHER'
        )
    ),

    is_active INTEGER NOT NULL DEFAULT 1
        CHECK (is_active IN (0, 1)),

    UNIQUE(name, type)
);


CREATE TABLE IF NOT EXISTS CATEGORY (
    id INTEGER PRIMARY KEY,
    name TEXT NOT NULL UNIQUE,
    color TEXT
);


CREATE TABLE IF NOT EXISTS "TRANSACTION" (
    id INTEGER PRIMARY KEY,
    month_id INTEGER NOT NULL,
    bank_account_id INTEGER NOT NULL,
    category_id INTEGER,
    date TEXT NOT NULL,
    value NUMERIC NOT NULL,
    type TEXT NOT NULL CHECK (
        type IN (
            'INCOME',
            'EXPENSE',
            'INVESTMENT',
            'REDEMPTION'
        )
    ),
    payment_method TEXT NOT NULL CHECK (
        payment_method IN (
            'PIX',
            'DEBIT_CARD',
            'CREDIT_CARD',
            'CASH',
            'BANK_TRANSFER',
            'BOLETO'
        )
    ),
    description TEXT,

    CONSTRAINT chk_value_positive
        CHECK (value > 0),

    CONSTRAINT fk_transaction_month
        FOREIGN KEY (month_id)
        REFERENCES MONTH(id)
        ON DELETE NO ACTION
        ON UPDATE NO ACTION,

    CONSTRAINT fk_transaction_bank_account
        FOREIGN KEY (bank_account_id)
        REFERENCES BANK_ACCOUNT(id)
        ON DELETE NO ACTION
        ON UPDATE NO ACTION,

    CONSTRAINT fk_transaction_category
        FOREIGN KEY (category_id)
        REFERENCES CATEGORY(id)
        ON DELETE SET NULL
        ON UPDATE NO ACTION
);