import {AfterViewInit, Component, OnDestroy, ViewChild} from '@angular/core';
import {BreakpointObserver} from '@angular/cdk/layout';
import {BehaviorSubject, Subject} from 'rxjs';
import {takeUntil} from 'rxjs/operators';
import {GameService} from "./game.service";
import {Game} from "../domain/game";
import {Move} from "../domain/move";
import {BoardComponent} from "../board/board.component";
import {MoveInfoComponent} from "../move-info/move-info.component";
import {DiskEvent} from "../domain/disk-event";

@Component({
  selector: 'app-game',
  templateUrl: './game.component.html',
  styleUrls: ['./game.component.scss']
})
export class GameComponent implements AfterViewInit, OnDestroy {

  game: Game;
  moves: Move[];
  gamePaused$: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(false);

  @ViewChild(BoardComponent, {static: false}) board: BoardComponent;
  @ViewChild(MoveInfoComponent, {static: true}) moveInfo: MoveInfoComponent;

  private unsubscribe: Subject<void> = new Subject<void>();

  constructor(private readonly breakpointObserver: BreakpointObserver,
              private readonly gameService: GameService) {
  }

  ngAfterViewInit(): void {
    this.startGame();
  }

  startGame() {
    this.gameService.startGame()
      .pipe(
        takeUntil(this.unsubscribe)
      )
      .subscribe((game: Game) => {
        this.game = game;
        this.moves = game.currentTurn.possibleMoves;
        this.moveInfo.startTimer();
        setTimeout(() => {
          this.startGamePausedListener();
          this.startMovesListener();
        }, 1000);
      });
  }

  ngOnDestroy(): void {
    this.unsubscribe.next();
    this.unsubscribe.complete();
  }

  private startGamePausedListener() {
    this.gamePaused$
      .pipe(takeUntil(this.unsubscribe))
      .subscribe((paused: boolean) => {
        if (paused) {
          this.board.lock();
        } else {
          this.board.unlock();
        }
      });
  }

  private startMovesListener() {
    this.board.diskEvents$
      .pipe(takeUntil(this.unsubscribe))
      .subscribe((event: DiskEvent) => {
        if (event.type === 'MOVE') {
          this.gameService.moveDisk(this.game, event.move)
            .pipe(takeUntil(this.unsubscribe))
            .subscribe((game: Game) => {
              this.game = game;
              this.moves = game.currentTurn.possibleMoves;
            });
        }
      });
  }
}
