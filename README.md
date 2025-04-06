# Algorithm and Data Structure: Implementation and Validation of a Merkle Tree Based System

## Overview

This repository contains the implementation of the second project for the **Algorithms and Data Structures Lab** course (2024/2025) at the **University of Camerino**, under the guidance of Professor **Luca Tesei**.
This project is an implementation of a Merkle Tree System, 
designed to ensure data integrity and authenticity. 
The Merkle Tree is a binary tree structure where each 
leaf node represents the hash of a data block, 
and each non-leaf node represents the hash of its child nodes. 
This structure is particularly useful in distributed systems like blockchain, 
where verifying the integrity of data without transferring the entire dataset is crucial.

## Implementation Details

- **Hash Calculation**: The `HashUtil` class provides methods for computing MD5 hashes. The dataToHash method is used for hashing individual data blocks (leaf nodes), while computeMD5 is used for combining hashes in branch nodes to construct the tree.
- **Tree Construction**: The `MerkleTree` class builds a balanced tree from a `HashLinkedList` of data blocks, ensuring proper structure even if the number of blocks is not a power of two.
- **Proof Generation**: The `MerkleProof` class generates proofs by traversing the tree from the target leaf to the root, collecting the necessary hashes along the way to verify inclusion.

## Classes

1. **HashUtil**: Provides methods for MD5 hash calculation.
2. **HashLinkedList**: A linked list that integrates MD5 hash calculation for each element.
3. **MerkleNode**: Represents a node in the Merkle Tree, either a leaf or a branch.
4. **MerkleProof**: Manages Merkle proofs for verifying data inclusion.
5. **MerkleTree**: Represents and manages a complete Merkle Tree.

## Dependencies
The project uses only standard Java SE 1.8 libraries. No external dependencies are required.

## Testing
The project includes JUnit 5 test cases to verify the correctness of the implementation. Run the provided test classes to ensure that your implementation meets the requirements.

## Credits

This project was developed as part of the **Algorithms and Data Structures Lab** course at the **University of Camerino**. The project template and instructions were provided by **Professor Luca Tesei**. Special thanks to Professor Tesei for his guidance and support throughout the course.

## Contact

For any questions or issues, please refer to the project documentation or contact me.