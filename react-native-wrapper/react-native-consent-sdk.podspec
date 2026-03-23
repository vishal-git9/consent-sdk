require "json"

package = JSON.parse(File.read(File.join(__dir__, "package.json")))

Pod::Spec.new do |s|
  s.name         = "react-native-consent-sdk"
  s.version      = package["version"]
  s.summary      = package["description"]
  s.homepage     = package["repository"] || "https://github.com/vishal-git9/consent-sdk"
  s.license      = package["license"]
  s.authors      = package["author"]

  s.platforms    = { :ios => "13.0" }
  s.source       = { :git => "https://github.com/vishal-git9/consent-sdk.git", :tag => "#{s.version}" }

  s.source_files = "ios/**/*.{h,m,mm,swift}"

  s.dependency "React-Core"
  s.dependency "ConsentSDK"
end
