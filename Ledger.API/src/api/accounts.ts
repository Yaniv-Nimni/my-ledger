import express from "express";
import {CreateNewAccount, GetAccountInfoById} from '../controllers/accounts'
const router = express.Router();

router.post( "/", CreateNewAccount);
router.get( "/:id", GetAccountInfoById);

export default router