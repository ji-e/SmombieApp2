#!/bin/sh
# 배포 스크립트
#   ./deploy.sh test "릴리스 노트"  → 디버그 APK를 Firebase App Distribution pickstudio 그룹에 배포
#   ./deploy.sh store              → 출시 서명된 AAB 생성 (플레이 콘솔에 올릴 파일)
set -e
cd "$(dirname "$0")"
export JAVA_HOME="${JAVA_HOME:-/Applications/Android Studio.app/Contents/jbr/Contents/Home}"

FIREBASE_APP=1:671432941184:android:4ef51a706021210ea09065
FIREBASE_PROJECT=smombie-picka

case "$1" in
  test)
    ./gradlew assembleDebug -q
    firebase appdistribution:distribute app/build/outputs/apk/debug/app-debug.apk \
      --app "$FIREBASE_APP" --project "$FIREBASE_PROJECT" \
      --groups pickstudio --release-notes "${2:-테스트 빌드}"
    ;;
  store)
    if ! grep -q '^storePassword=.' keystore.properties 2>/dev/null || ! grep -q '^keyPassword=.' keystore.properties; then
      echo "keystore.properties의 storePassword, keyPassword를 먼저 채워 주세요." >&2
      exit 1
    fi
    ./gradlew bundleRelease -q
    echo "플레이 콘솔에 올릴 파일: $(pwd)/app/build/outputs/bundle/release/app-release.aab"
    ;;
  *)
    echo "사용법: ./deploy.sh test [릴리스 노트] | ./deploy.sh store" >&2
    exit 1
    ;;
esac
