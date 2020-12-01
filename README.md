# ERC-20 TOKEN SNAPSHOT

ERC-20 TOKEN SNAPSHOT exports balance of token holders of erc-20 smart contract at a specific block to csv file.

There are two modes to create a snapshot.
1. **ARCHIVED** You will need an archived node address,
 since the application requests for the historical balance of the contract.
 
2. **EVENT** Requires no special node. Collects balance from transfer events.
 Works with Infura free plan.

## Running the application
Run from command line `./gradlew clean build` to build jar file.

Then `java -jar build/libs/snanshot-0.0.1-SNAPSHOT.jar --pass arguments below` to start

Input arguments:
* `-n, --node-address`      Server url of node connected to [_required_]
* `-c, --contract`          Address of token Smart contract [_required_]
* `-f, --from`              Start block number [_optional_, default is 0]
* `-t, --to`                End block number [_optional, default is **latest block**_]
* `-d, --decimals`          Snapshot decimals value [_optional_, default is **8**]
* `-o, --output`            Name of csv file to save [_optional_, default is **snapshot_at_block_toBlocknumber**]
* `-m, --mode`              Snapshot mode [_optional_, possible values are ARCHIVED and EVENT, default is **EVENT**]
* `-h, --help`              Show available options and exit

Output:
* _clear_Your_File_Name.csv named csv file with headers **ADDRESS, BALANCE**
