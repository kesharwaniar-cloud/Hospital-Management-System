const express = require('express');
const mysql = require('mysql2');
const cors = require('cors');

const app = express();
app.use(cors());
app.use(express.json());

const db = mysql.createConnection({
    host: 'localhost',
    user: 'root',
    password: 'kesharwani123@',
    database: 'hospital_db'
});

db.connect((err) => {
    if (err) {
        console.log('DB Error:', err);
    } else {
        console.log('Database Connected!');
    }
});

// Admit Patient
app.post('/admit', (req, res) => {
    const { name, disease } = req.body;
    const query = 'INSERT INTO patients (name, disease) VALUES (?, ?)';
    db.query(query, [name, disease], (err, result) => {
        if (err) return res.json({ message: 'Error: ' + err });
        res.json({ message: name + ' admitted successfully!' });
    });
});

// Discharge Patient
app.delete('/discharge/:name', (req, res) => {
    const name = req.params.name;
    const query = 'DELETE FROM patients WHERE name = ?';
    db.query(query, [name], (err, result) => {
        if (err) return res.json({ message: 'Error: ' + err });
        res.json({ message: name + ' discharged successfully!' });
    });
});

// Display All Patients
app.get('/patients', (req, res) => {
    db.query('SELECT * FROM patients', (err, results) => {
        if (err) return res.json({ message: 'Error: ' + err });
        res.json(results);
    });
});

app.listen(3000, () => {
    console.log('Server running on port 3000');
});