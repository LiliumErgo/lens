# LENS
Ergo NFT debugging tool.

### KYA

1. Use at your own risk.

### Installing

1. Download the latest release to minimize your own risk, or clone/download repository if you are adventurous. 
2. Install Java (JRE, JDK, or OpenJDK).
3. If you would like to compile the jar yourself, download sbt and run `sbt assembly` within the repository/source folder.

### Usage

#### Mint

1. Run `java -jar lens-<version>.jar --mint <network-type> <mint-type> <mnemonic> <wallet-address>`
2. `<network-type>` is the network you want to use to execute this transaction, either `"mainnet"` or `"testnet"`. If you select testnet, please make sure the requisite command-line arguments are valid testnet arguments.
3. `<mint-type>` is the mint type, either `"single"` or `"collection"`.
4. `<mnemonic>` is your seed phrase used for signing all the required transactions.
5. `<wallet-address>` is the address where all your funds come from and where the NFTs will go. This should correspond to the mnemonic, but it can be a derived address if you wish.
6. Please make sure to place the NFT metadata file within the `data` folder and call it `nft_metadata.json`. If you are doing a collection mint, please include the collection metadata in a file called `collection_metadata.json`.
7. If the transaction is submitted successfully to the network, a link to see the transaction on the explorer will be displayed. Wait until the transaction is confirmed to see token details displayed.

#### Print

1. Run `java -jar lens-<version>.jar --print <network-type> <box-type> <box-id>`
2. `<network-type>` is the network you want to use to execute this transaction, either `"mainnet"` or `"testnet"`. If you select testnet, please make sure the requisite command-line arguments are valid testnet arguments.
3. `<box-type>` is the token mint box type, one of: `"eip4issuance"`, `"eip24issuer"`, `"eip34issuer"`.
4. `<box-id>` is the box id of the box you want to print, corresponding to the box-type parameter.

#### Snapshot

1. Run `java -jar lens-<version>.jar --snapshot <network-type> <collection-id>`
2. `<network-type>` is the network you want to use to execute this transaction, either `"mainnet"` or `"testnet"`. If you select testnet, please make sure the requisite command-line arguments are valid testnet arguments.
3. `<collection-id>` is the collection token id.

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