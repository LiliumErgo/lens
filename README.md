# minting-for-dummies
Super basic NFT minter for dummies.

### KYA

1. Use at your own risk.

### Installing

1. Download the latest release to minimize your own risk, or clone/download repository if you are adventurous. 
2. Install Java (JRE, JDK, or OpenJDK).
3. If you would like to compile the jar yourself, download sbt and run `sbt assembly` within the repository/source folder.

### Usage

#### Minting

1. Run `java -jar minting-for-dummies-<version>.jar --mint <network-type> <token-name> <token-description> <token-content> <token-content-link> <input-box-id> <wallet-address> <mnemonic>`
2. `<network-type>` is the network you want to use to execute this transaction, either `"mainnet"` or `"testnet"`. If you select testnet, please make sure the requisite command-line arguments are valid testnet arguments.
3. `<token-name` is the name of the token you would like your NFT to have.
4. `<token-description>` is the brief description of the token.
5. `<token-content>` is the image data/file representing the NFT, this will be hashed in the project.
6. `<token-content-link>` is a link to the image for everyone to look at.
7. `<input-box-id>` is the box id of the input box to the issuer creation transaction. The created issuer box will become the input to the minting transaction itself. The transaction id of the issuer box will become the token id of your token. Note that this project assumes all addresses involved are your P2PK address.
8. `<wallet-address>` is the wallet address under which the input eUTXO box belongs to. Make sure this box contains MORE than 0.112 ERG. Note, please make sure that the address used is the first address created from your wallet, not any other derived address.
9. `<mnemonic>` is the seed phrase used in the creation of your wallet. This is needed for signing the minting transaction.
10. Please insert command-line arguments as strings with `""`
11. If the transaction is submitted successfully to the network, a link to see the transaction on the explorer will be displayed. Wait until the transaction is confirmed to see token details displayed.

#### Printing

1. Run `java -jar --print <network-type> <box-type> <nft-box-id>`
2. `<network-type>` is the network you want to use to execute this transaction, either `"mainnet"` or `"testnet"`. If you select testnet, please make sure the requisite command-line arguments are valid testnet arguments.
3. `<box-type>` is the token mint box type, either `"eip24issuer"` or `"eip4issuance"`.
4. `<nft-box-id>` is the box id corresponding to the box you want to print.

### Fees

1. For this project, the miner fee is hard-coded to be the minimum fee of 0.001 ERG and the issuance box eUTXO containing the NFT token will have 0.01 ERG. So, as stated previously, make sure the input box contains enough ERG. Any excess ERG is sent back as change to your wallet in a new eUTXO.
2. Fee Distribution
   - Miner Fee: 0.001 ERG
   - Issuer Box: 100*MinerFee = 0.1 ERG
   - Issuance Box: 10*MinerFee = 0.01 ERG
   - Total: 2*MinerFee + IssuerBox + IssuanceBox = 0.112 ERG

### Learning Objectives

1. Learn the basics of how to mint an EIP-4 and EIP-24 compliant picture NFT using Ergo AppKit with a Scala backend.
2. Learn about simple chained transactions.

### Limitations

1. This project has hard-coded metadata for the issuer box since it is sort of complicated to encode the metadata. However, using this project as an example should help in implementing custom metadata for your tokens, and will involve writing your own custom metadata encoder.
2. The token content is just a meaningless input string, but in practice your project should implement a way to access the file data itself, which is then hashed and stored in the issuance box.
3. The token link is also a meaningless input string, unless you insert an active link.
4. This project only implements EIP-4 and EIP-24, not the collection standard outlined in EIP-34. However, hopefully this project serves as a good guide for how you might do this on your own :)

### Misc

1. This project is meant to be used for pedagogical purposes and emulates the minting of a "picture" NFT, following the EIP-4 standard, so only ONE token will exist with the given token id, i.e. a singleton token. In general, a token can have an amount greater than one when created. If you feel like doing that, then modify the corresponding code section appropriately. There is no magic to how tokens work on Ergo, it is all very straightforward conceptually. Try not to get lost in the language, just look directly at how they are implemented, and it will all make sense.
2. Relevant EIPs you may like to follow up on are the following, from which you can take this project as an example and build upon for more complex use cases:
   - Asset Standard: [EIP-4](https://github.com/ergoplatform/eips/blob/master/eip-0004.md)
   - Artwork Standard: [EIP-24](https://github.com/ergoplatform/eips/blob/master/eip-0024.md)
   - Collection Standard: [EIP-34](https://github.com/ergoplatform/eips/blob/master/eip-0034.md)

### Reporting Issues

Slide into my DMs.