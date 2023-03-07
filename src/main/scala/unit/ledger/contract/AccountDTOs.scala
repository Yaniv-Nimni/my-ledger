package unit.ledger.contract

import zio.json.{DeriveJsonCodec, JsonCodec}

object AccountDTOs {

  case class ReturnedAccountObject(id: Int, name: String, balance: Int, accountType: String)
  object ReturnedAccountObject {
    implicit val codec: JsonCodec[ReturnedAccountObject] = DeriveJsonCodec.gen[ReturnedAccountObject]
  }

  case class CreateAccountRequest(name: String, balance: Int = 0)
  object CreateAccountRequest {
    implicit val codec: JsonCodec[CreateAccountRequest] = DeriveJsonCodec.gen[CreateAccountRequest]
  }
}
