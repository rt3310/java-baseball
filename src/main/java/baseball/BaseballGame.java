package baseball;

import camp.nextstep.edu.missionutils.Console;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

public class BaseballGame {
    private static final String GAME_START_MESSAGE = "숫자 야구 게임을 시작합니다.";
    private static final String INPUT_NUMBER_MESSAGE = "숫자를 입력해주세요 : ";
    private static final String REPLAY_MESSAGE = "게임을 새로 시작하려면 1, 종료하려면 2를 입력하세요.";
    private static final int NUMBER_OF_BALLS = 3;
    private static final int REPLAY = 1;
    private static final int OVER = 2;

    private Hitter hitter;
    private Pitcher pitcher;
    private Referee referee;

    public BaseballGame(Hitter hitter, Pitcher pitcher, Referee referee) {
        this.hitter = hitter;
        this.pitcher = pitcher;
        this.referee = referee;
    }

    public void playGame(boolean isPlay) {
        System.out.println(GAME_START_MESSAGE);
        while (isPlay) {
            pitcher.initThrownBallList();
            isPlay = playInning();
        }
    }

    private boolean playInning() {
        List<Ball> pitcherBalls = pitcher.throwRandomBalls(NUMBER_OF_BALLS);
        while (true) {
            referee.initCount();
            referee.judgeGameResult(hitter.hitBalls(inputNumber()), pitcherBalls);
            printResult();

            if (isGameOver()) {
                return isReplaying();
            }
        }
    }

    public String inputNumber() {
        System.out.print(INPUT_NUMBER_MESSAGE);
        String number = Console.readLine();
        validate(number);
        return number;
    }


    private boolean isGameOver() {
        if (referee.getStrikeCount() == NUMBER_OF_BALLS) {
            System.out.println(NUMBER_OF_BALLS + "개의 숫자를 모두 맞히셨습니다! 게임 종료");
            return true;
        }
        return false;
    }

    private boolean isReplaying() {
        System.out.println(REPLAY_MESSAGE);
        Integer number = Integer.valueOf(Console.readLine());

        return number.equals(REPLAY);
    }

    private void printResult() {
        int ballCount = referee.getBallCount();
        int strikeCount = referee.getStrikeCount();

        printBallCount(ballCount);
        printStrikeCount(strikeCount);
        printNothing(ballCount, strikeCount);
        System.out.println();
    }

    private void printBallCount(int ballCount) {
        if (ballCount > 0) {
            System.out.print(ballCount + "볼 ");
        }
    }

    private void printStrikeCount(int strikeCount) {
        if (strikeCount > 0) {
            System.out.print(strikeCount + "스트라이크");
        }
    }

    private void printNothing(int ballCount, int strikeCount) {
        if (ballCount == 0 && strikeCount == 0) {
            System.out.print("낫싱");
        }
    }

    private void validate(String number) {
        validateNumberLength(number);
        validateNumberRange(number);
        validateNumberDuplication(number);
    }

    private void validateNumberLength(String number) {
        if (number.length() != NUMBER_OF_BALLS) {
            throw new IllegalArgumentException(NUMBER_OF_BALLS + "자리 숫자만 입력 가능합니다.");
        }
    }

    private void validateNumberRange(String number) {
        if (Pattern.compile("[^1-9]").matcher(number).find()) {
            throw new IllegalArgumentException("1과 9사이의 숫자만 입력 가능합니다.");
        }
    }

    private void validateNumberDuplication(String number) {
        if (IntStream.range(0, 10)
                .anyMatch(digit -> getDigitCount(number, digit) >= 2)) {
            throw new IllegalArgumentException("중복된 숫자가 있습니다.");
        }
    }

    private long getDigitCount(String number, int digit) {
        return Arrays.stream(number.split(""))
                .filter(digitString -> digitString.equals(String.valueOf(digit)))
                .count();
    }
}
