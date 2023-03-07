import axios from "axios";
import express from 'express'
import { transactionsJsonApi } from '../Utils/utils'
const { convertResponseToJsonApi, extractFromJsonApi, convertMultiResponseToJsonApi } = transactionsJsonApi

export const GetAllTransactionsByAccountId: express.RequestHandler = async (req, res, next) => {
    // render the index template
    try {
        const response = await axios.get(`http://localhost:3334/transactions/${req.params.accountId}`)
        res.json(convertMultiResponseToJsonApi(response.data));
    } catch(e) {
        res.sendStatus(400)
    }
}

export const MakeTransaction: express.RequestHandler = async (req, res, next) => {
    // render the index template
    try {
        const payloadAsSimpleObject = extractFromJsonApi(req.body)
        const response = await axios.post('http://localhost:3334/transactions', payloadAsSimpleObject)
        res.json(convertResponseToJsonApi(response.data));
    } catch(e) {
        res.sendStatus(400)
    }
}