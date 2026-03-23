Pod::Spec.new do |s|
  s.name             = 'ConsentSDK'
  s.version          = '1.0.0'
  s.summary          = 'A privacy and content consent popup SDK.'
  s.description      = <<-DESC
    ConsentSDK provides native UI screens and networking features to manage
    user privacy decisions, GDPR consent, and analytics permissions.
  DESC
  s.homepage         = 'https://github.com/vishal-git9/consent-sdk'
  s.license          = { :type => 'MIT', :file => '../LICENSE' }
  s.author           = { 'Vishal Singh' => 'example@example.com' }
  s.source           = { :git => 'https://github.com/vishal-git9/consent-sdk.git', :tag => s.version.to_s }

  s.ios.deployment_target = '13.0'
  s.swift_version = '5.0'

  s.source_files = 'Sources/ConsentSDK/**/*'
end
