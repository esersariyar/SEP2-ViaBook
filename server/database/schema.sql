DROP TABLE IF EXISTS working_hours CASCADE;
DROP TABLE IF EXISTS appointments CASCADE;
DROP TABLE IF EXISTS dentist_profiles CASCADE;
DROP TABLE IF EXISTS users CASCADE;

CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password TEXT NOT NULL,
    role VARCHAR(50) NOT NULL CHECK (role IN ('patient', 'dentist', 'secretary')),
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE dentist_profiles (
    id SERIAL PRIMARY KEY,
    user_id INT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    description TEXT,
    specialization VARCHAR(255),
    CONSTRAINT dentist_profiles_user_role_check 
        CHECK ((SELECT role FROM users WHERE id = user_id) = 'dentist')
);

CREATE TABLE appointments (
    id SERIAL PRIMARY KEY,
    patient_id INT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    dentist_id INT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    appointment_time TIMESTAMP NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'pending' CHECK (status IN ('pending', 'approved', 'cancelled')),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT appointments_patient_role_check 
        CHECK ((SELECT role FROM users WHERE id = patient_id) = 'patient'),
    CONSTRAINT appointments_dentist_role_check 
        CHECK ((SELECT role FROM users WHERE id = dentist_id) = 'dentist')
);

CREATE TABLE working_hours (
    id SERIAL PRIMARY KEY,
    dentist_id INT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    day_of_week VARCHAR(20) NOT NULL CHECK (day_of_week IN ('Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday', 'Sunday')),
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    CONSTRAINT working_hours_dentist_role_check 
        CHECK ((SELECT role FROM users WHERE id = dentist_id) = 'dentist'),
    CONSTRAINT working_hours_time_check 
        CHECK (start_time < end_time),
    UNIQUE(dentist_id, day_of_week)
);

CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_role ON users(role);
CREATE INDEX idx_dentist_profiles_user_id ON dentist_profiles(user_id);
CREATE INDEX idx_appointments_patient_id ON appointments(patient_id);
CREATE INDEX idx_appointments_dentist_id ON appointments(dentist_id);
CREATE INDEX idx_appointments_time ON appointments(appointment_time);
CREATE INDEX idx_appointments_status ON appointments(status);
CREATE INDEX idx_working_hours_dentist_id ON working_hours(dentist_id);
CREATE INDEX idx_working_hours_day ON working_hours(day_of_week); 