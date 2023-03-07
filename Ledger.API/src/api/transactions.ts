import express from "express";
import {MakeTransaction, GetAllTransactionsByAccountId} from '../controllers/transactions'
const router = express.Router();

router.post( "/", MakeTransaction)
router.get( "/:accountId", GetAllTransactionsByAccountId);

export default router