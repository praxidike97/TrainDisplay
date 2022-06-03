#include <QApplication>
#include "PlatformInfo.h"

int main(int argc, char *argv[]) {
    QApplication app(argc, argv);

    PlatformInfo platformInfo;
    platformInfo.show();

    return app.exec();
}
