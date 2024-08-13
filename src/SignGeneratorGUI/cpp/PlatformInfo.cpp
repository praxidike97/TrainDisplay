//
// Created by fabian on 30.05.22.
//
#include <iostream>

#include <QtGui>
#include <QLabel>
#include <QGridLayout>
#include <QFileDialog>

#include <opencv2/opencv.hpp>
#include <opencv2/freetype.hpp>

#include "PlatformInfo.h"

PlatformInfo::PlatformInfo(QWidget *parent) : QWidget(parent) {
    QLabel *departTimeLabel = new QLabel("Abfahrtszeit:");
    departTimeLine = new QLineEdit;

    QLabel *trainCodeLabel = new QLabel("Zugcode:");
    trainCodeLine = new QLineEdit;

    QLabel *destinationNameLabel = new QLabel("Zielort:");
    destinationNameLine = new QLineEdit;

    imageLabel = new QLabel;

    generateSignButton = new QPushButton("Anzeige generieren", this);
    connect(generateSignButton, &QPushButton::released, this, &PlatformInfo::onClickGenerateSignButton);

    saveSignButton = new QPushButton("Anzeige speichern", this);

    QGridLayout *mainLayout = new QGridLayout;
    mainLayout->addWidget(departTimeLabel, 0, 0);
    mainLayout->addWidget(departTimeLine, 0, 1);

    mainLayout->addWidget(trainCodeLabel, 1, 0, Qt::AlignTop);
    mainLayout->addWidget(trainCodeLine, 1, 1);

    mainLayout->addWidget(destinationNameLabel, 2, 0, Qt::AlignTop);
    mainLayout->addWidget(destinationNameLine, 2, 1);

    mainLayout->addWidget(generateSignButton, 3, 1);

    imageLabel->setSizePolicy(QSizePolicy::Expanding,QSizePolicy::Expanding);
    mainLayout->addWidget(imageLabel, 4, 1);

    mainLayout->addWidget(saveSignButton, 5, 1);
    connect(saveSignButton, &QPushButton::released, this, &PlatformInfo::onClickSaveSignButton);
    saveSignButton->hide();

    setLayout(mainLayout);
    setWindowTitle("Bahnsteiganzeigen");
    this->resize(300, this->heightMM());
}

void PlatformInfo::onClickGenerateSignButton(){
    img = cv::Mat(80, 160, CV_8UC3, cv::Scalar(20, 85, 192));

    cv::Ptr<cv::freetype::FreeType2> ft2;
    ft2 = cv::freetype::createFreeType2();
    ft2->loadFontData("./NimbusSanL-Reg.otf", 0);

    ft2->putText(img, departTimeLine->text().toStdString(), cv::Point(8, 20), 18, cv::Scalar::all(255), -1, 16, true );
    ft2->putText(img, trainCodeLine->text().toStdString(), cv::Point(8, 35), 11, cv::Scalar::all(255), -1, 16, true );
    ft2->putText(img, destinationNameLine->text().toStdString(), cv::Point(8, 70), 22, cv::Scalar::all(255), -1, 16, true );

    imageLabel->setPixmap(QPixmap::fromImage(QImage(img.data, img.cols, img.rows, img.step, QImage::Format_RGB888)));

    saveSignButton->show();
}

void PlatformInfo::onClickSaveSignButton(){

     QString filename =  QFileDialog::getSaveFileName(
             this,
             "Bitmap speichern",
             QDir::currentPath(),
             "BMP files (*.BMP)");

     if (!filename.endsWith(".bmp", Qt::CaseInsensitive)){
         filename += ".bmp";
     }

     cv::Mat saveImg(80, 160, CV_8UC3);
     cv::cvtColor(img, saveImg, cv::COLOR_BGR2RGB);
     cv::imwrite(filename.toUtf8().toStdString(), saveImg);
}