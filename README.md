# LENS
NFT debugging tool.

### KYA

1. Use at your own risk.

### Installing

1. Download the latest release to minimize your own risk, or clone/download repository if you are adventurous. 
2. Install Java (JRE, JDK, or OpenJDK).
3. If you would like to compile the jar yourself, download sbt and run `sbt assembly` within the repository/source folder.

### Usage

#### Printing

1. Run `java -jar --print <network-type> <box-type> <box-id>`
2. `<network-type>` is the network you want to use to execute this transaction, either `"mainnet"` or `"testnet"`. If you select testnet, please make sure the requisite command-line arguments are valid testnet arguments.
3. `<box-type>` is the token mint box type, one of: `"eip4issuance"`, `"eip24issuer"`, `"eip34issuer"`.
4. `<box-id>` is the box id of the box you want to print, corresponding to the box-type parameter.

#### Snapshot

1. Run `java -jar --snapshot <network-type> <collection-id>`
2. `<network-type>` is the network you want to use to execute this transaction, either `"mainnet"` or `"testnet"`. If you select testnet, please make sure the requisite command-line arguments are valid testnet arguments.
3. `<collection-id>` is the collection token id.

### Misc

1. Relevant EIPs you may like to follow up on are the following:
   - Asset Standard: [EIP-4](https://github.com/ergoplatform/eips/blob/master/eip-0004.md)
   - Artwork Standard: [EIP-24](https://github.com/ergoplatform/eips/blob/master/eip-0024.md)
   - Collection Standard: [EIP-34](https://github.com/ergoplatform/eips/blob/master/eip-0034.md)

### Reporting Issues

Slide into my DMs.