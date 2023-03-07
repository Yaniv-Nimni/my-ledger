import accountsRoute from './accounts'
import transactionsRoute from './transactions'
import express from 'express'
const router = express.Router()

router.use('/accounts', accountsRoute)
router.use('/transactions', transactionsRoute)

export default router

