#include <QWidget>
#include <QLineEdit>
#include <QTextEdit>
#include <QPushButton>
#include <QPlainTextEdit>
#include <QLabel>

#include <opencv2/opencv.hpp>

class PlatformInfo : public QWidget
{
    Q_OBJECT

public:
    PlatformInfo(QWidget *parent = nullptr);

private:
    cv::Mat img;

    QLineEdit *departTimeLine;
    QLineEdit *trainCodeLine;
    QLineEdit* destinationNameLine;
    QLabel* imageLabel;

    QPushButton* generateSignButton;
    QPushButton* saveSignButton;

    void onClickGenerateSignButton();
    void onClickSaveSignButton();
};
