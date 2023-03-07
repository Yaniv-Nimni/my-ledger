import axios from "axios";
import express from 'express'
import { accountsJsonApi } from '../Utils/utils'
const { convertResponseToJsonApi, extractFromJsonApi } = accountsJsonApi

export const GetAccountInfoById: express.RequestHandler = async (req, res, next) => {
    // render the index template
    try {
        const response = await axios.get(`http://localhost:3334/accounts/${req.params.id}`)
        res.json(convertResponseToJsonApi(response.data));
    } catch (e) {
        res.sendStatus(400)
    }
}

export const CreateNewAccount: express.RequestHandler = async (req, res, next) => {
    // render the index template
    try {
        const payloadAsSimpleObject = extractFromJsonApi(req.body)
        const response = await axios.post('http://localhost:3334/accounts', payloadAsSimpleObject)
        res.json(convertResponseToJsonApi(response.data));
    } catch (e) {
        res.sendStatus(400)
    }
}