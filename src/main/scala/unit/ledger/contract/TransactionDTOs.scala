package unit.ledger.contract

import zio.json.{DeriveJsonCodec, JsonCodec}

object TransactionDTOs {

  case class ReturnedTransactionObject(id: Int,
                                       txType: String,
                                       amount: Int,
                                       accountId: Int,
                                       counterpartyId: Int = 1)

  object ReturnedTransactionObject {
    implicit val codec: JsonCodec[ReturnedTransactionObject] = DeriveJsonCodec.gen[ReturnedTransactionObject]
  }


  case class CreateTransactionRequest(accountId: Int, transactionType: String, amount: Int, counterpartyId: Int = 1)
  object CreateTransactionRequest {
    implicit val codec: JsonCodec[CreateTransactionRequest] = DeriveJsonCodec.gen[CreateTransactionRequest]
  }
}
