const functions = require('firebase-functions');
const admin = require('firebase-admin');
const express = require('express');
const cors = require('cors');
const app = express();

app.use(cors({ origin: true }));

app.get('/test', (req, res) => {
    return res.status(200).send('Testing succeeded!');
})

var serviceAccount = require("./permissions.json");
admin.initializeApp({
    credential: admin.credential.cert(serviceAccount),
    databaseURL: "https://ausm-77984.firebaseio.com"
});

const db = admin.firestore();

exports.app = functions.https.onRequest(app);

// create user
app.post('/user/create', (req, res) => {
    // (async () => {
    try {
        let addDoc = db.collection('users')
            .add({
                    "user_name_str": req.body.user_name_str,
                    "user_password_str": req.body.user_password_str
            }).then(ref => {
                console.log("Addeded document iwth ID: ", ref.id);
            })
        return res.status(200).send();
    } catch (error) {
        console.log(error);
        return res.status(500).send(error);
    }
    // });
})

// check user
app.get('/user/get', (req, res) => {
    (async () => {
        try {
            let query = db.collection('users');
            let response = [];
        
            await query.get().then((snapshot) => {
                let docs = snapshot.docs;
                for (let doc of docs) {
                    console.log(doc);
                    const selectedItem = {
                        id: doc.id,
                        item: doc.data()
                    };
        
                    response.push(selectedItem);
                }
            });
        
            return res.status(200).send(response);
        } catch (error) {
            console.log(error);
            return res.status(500).send(error);
        }
    })();
});