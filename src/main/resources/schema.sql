CREATE TABLE IF NOT EXISTS todos (
                                     id INTEGER PRIMARY KEY AUTOINCREMENT,
                                     description TEXT NOT NULL,
                                     completion_status TEXT NOT NULL DEFAULT 'PENDING'
);
