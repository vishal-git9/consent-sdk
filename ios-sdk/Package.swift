// swift-tools-version: 5.9

import PackageDescription

let package = Package(
    name: "ConsentSDK",
    platforms: [
        .iOS(.v15),
        .macOS(.v12)
    ],
    products: [
        .library(
            name: "ConsentSDK",
            targets: ["ConsentSDK"]
        ),
    ],
    targets: [
        .target(
            name: "ConsentSDK",
            path: "Sources/ConsentSDK"
        ),
        .testTarget(
            name: "ConsentSDKTests",
            dependencies: ["ConsentSDK"],
            path: "Tests/ConsentSDKTests"
        ),
    ]
)
