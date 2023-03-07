declare type JsonApiObject = {
    data: DataObject
}
declare type AccountJsonApiObject = JsonApiObject
declare type TransactionJsonApiObject = JsonApiObject
declare type MultiTransactionJsonApiObject = {
    data: DataObject[]
}

declare type DataObject = {
    type: AccountType | TransactionType,
    id?: number,
    attributes?: Attributes
    relationships?: Relationship
}
declare type AccountType = 'depositAccount'
declare type TransactionType = 'deposit' | 'withdraw' | 'bookPayment'

declare type Attributes = {
    [prop:string]: string | string[] | number | number[] | Attributes
}

declare type Relationship = {
    account: JsonApiObject
    counterpartyAccount: JsonApiObject | null
}

declare type FullName = {
    first: string,
    last: string
}

declare type BasicAccountObject = {
    name: string,
    balance: number
}

declare type AccountResponseObject = {
    id: number,
    name: string,
    balance: number
    accountType: AccountType
}

declare type BasicTransactionObject = {
    transactionType: TransactionType,
    amount: number,
    accountId: number,
    counterpartyId: number
}

declare type TransactionResponseObject = {
    transactionType: TransactionType,
    id: number,
    amount: number,
    accountId: number,
    counterpartyId?: number
}

export const accountsJsonApi = {
    extractFromJsonApi: function(jsonApiObject: AccountJsonApiObject): BasicAccountObject {
        const {first, last} = jsonApiObject.data.attributes.fullName as FullName
        const balance = jsonApiObject.data.attributes.balance as number

        return {
            name: `${first} ${last}`,
            balance
        }
    },
    convertResponseToJsonApi: function(responseObject: AccountResponseObject): AccountJsonApiObject {
        return {
            data: {
                type: responseObject.accountType,
                id: responseObject.id,
                attributes: {
                    name: responseObject.name,
                    balance: responseObject.balance
                }
            }
        }
    }
}
export const transactionsJsonApi = {
    extractFromJsonApi: function(jsonApiObject: TransactionJsonApiObject): BasicTransactionObject {
        const {type, attributes, relationships} = jsonApiObject.data
        return {
            transactionType: type as TransactionType,
            amount: attributes.amount as number,
            accountId: relationships.account.data.id,
            counterpartyId: relationships.counterpartyAccount?.data.id
        }
    },
    convertResponseToJsonApi: function(responseObject: TransactionResponseObject): TransactionJsonApiObject {
        const counterparty = responseObject.counterpartyId ?
            {
                data: {
                    type: "depositAccount", //counterparty will only show for bookPayments between two deposit accounts
                    id: responseObject.counterpartyId
                }
            } as JsonApiObject : null

        return {
            data: {
                type: responseObject.transactionType,
                id: responseObject.id,
                attributes: {
                    amount: responseObject.amount,
                    direction: "Credit"
                },
                relationships: {
                    account: {
                        data: {
                            type: "depositAccount",
                            id: responseObject.accountId
                        }
                    },
                    counterpartyAccount: counterparty
                }
            }
        }
    },
    convertMultiResponseToJsonApi: function(responseObjects: TransactionResponseObject[]): MultiTransactionJsonApiObject {
        const dataArray = responseObjects.map(responseObject => transactionsJsonApi.convertResponseToJsonApi(responseObject).data)
        return {
            data: dataArray
        }
    }
}