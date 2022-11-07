package baseball;

import camp.nextstep.edu.missionutils.Console;
import java.util.Arrays;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

public class BaseballGame {
    private static final int NUMBER_OF_BALLS = 3;
    private static final int REPLAY = 1;
    private static final String INVALID_NUMBER_RANGE_REGEX = "[^1-9]";

    private final Hitter hitter;
    private final Pitcher pitcher;
    private final Referee referee;

    public BaseballGame(Hitter hitter, Pitcher pitcher, Referee referee) {
        this.hitter = hitter;
        this.pitcher = pitcher;
        this.referee = referee;
    }

    public void playGame(boolean isPlay) {
        System.out.println(BaseballMessage.GAME_START_MESSAGE.getMessage());
        while (isPlay) {
            pitcher.initThrownBallList();
            isPlay = playInning();
        }
    }

    private boolean playInning() {
        Balls pitcherBalls = pitcher.throwRandomBalls(NUMBER_OF_BALLS);
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
        System.out.print(BaseballMessage.INPUT_NUMBER_MESSAGE.getMessage());
        String number = Console.readLine();
        validate(number);
        return number;
    }


    private boolean isGameOver() {
        if (referee.getStrikeCount() == NUMBER_OF_BALLS) {
            System.out.println(NUMBER_OF_BALLS + BaseballMessage.GAME_OVER_MESSAGE.getMessage());
            return true;
        }
        return false;
    }

    private boolean isReplaying() {
        System.out.println(BaseballMessage.REPLAY_MESSAGE.getMessage());
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
            System.out.print(ballCount + BaseballMessage.BALL_COUNT_MESSAGE.getMessage());
        }
    }

    private void printStrikeCount(int strikeCount) {
        if (strikeCount > 0) {
            System.out.print(strikeCount + BaseballMessage.STRIKE_COUNT_MESSAGE.getMessage());
        }
    }

    private void printNothing(int ballCount, int strikeCount) {
        if (ballCount == 0 && strikeCount == 0) {
            System.out.print(BaseballMessage.NOTHING_MESSAGE.getMessage());
        }
    }

    private void validate(String number) {
        validateNumberLength(number);
        validateNumberRange(number);
        validateNumberDuplication(number);
    }

    private void validateNumberLength(String number) {
        if (number.length() != NUMBER_OF_BALLS) {
            throw new IllegalArgumentException(NUMBER_OF_BALLS + BaseballMessage.INVALID_NUMBER_LENGTH_MESSAGE.getMessage());
        }
    }

    private void validateNumberRange(String number) {
        if (Pattern.compile(INVALID_NUMBER_RANGE_REGEX).matcher(number).find()) {
            throw new IllegalArgumentException(BaseballMessage.INVALID_NUMBER_RANGE_MESSAGE.getMessage());
        }
    }

    private void validateNumberDuplication(String number) {
        if (IntStream.range(0, 10)
                .anyMatch(digit -> getDigitCount(number, digit) >= 2)) {
            throw new IllegalArgumentException(BaseballMessage.DUPLICATED_NUMBER_MESSAGE.getMessage());
        }
    }

    private long getDigitCount(String number, int digit) {
        return Arrays.stream(number.split(""))
                .filter(digitString -> digitString.equals(String.valueOf(digit)))
                .count();
    }
}
