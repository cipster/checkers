import {
  AfterViewInit,
  Component,
  ElementRef,
  EventEmitter,
  Input,
  OnDestroy,
  OnInit,
  Output,
  QueryList,
  Renderer2,
  ViewChildren
} from '@angular/core';
import {Board} from "../domain/board";
import {DiskComponent} from "../disk/disk.component";
import {Move} from "../domain/move";
import {Subject} from "rxjs";
import {DiskEvent} from "../domain/disk-event";
import {takeUntil} from "rxjs/operators";
import {MatGridTile} from "@angular/material/grid-list";
import {Position} from "../domain/position";
import {Disk} from "../domain/disk";

const POSSIBLE_MOVE_SQUARE_CLASS = 'possible-move-square';

@Component({
  selector: 'app-board',
  templateUrl: './board.component.html',
  styleUrls: ['./board.component.scss']
})
export class BoardComponent implements OnInit, AfterViewInit, OnDestroy {
  @Input() board: Board;
  @Input() moves: Move[] = [];
  @ViewChildren(MatGridTile, {read: ElementRef}) squares: QueryList<ElementRef>;

  @Output() diskEvents$: EventEmitter<DiskEvent> = new EventEmitter<DiskEvent>();
  private unsubscribe: Subject<void> = new Subject<void>();
  private selectedDisk: Disk;

  constructor(private readonly ref: ElementRef,
              private readonly renderer: Renderer2) {
  }

  ngAfterViewInit(): void {
    this.diskEvents$
      .pipe(takeUntil(this.unsubscribe))
      .subscribe(diskEvent => {
        let selectedSquares = [];
        let possibleTargetSquares = [];
        let selectedDisk = diskEvent.disk;
        this.selectedDisk = selectedDisk;

        let movesForDisk = this.moves.filter((move: Move) => {
          return move.disk.id === selectedDisk.id;
        });

        this.squares.forEach(square => {
          this.renderer.removeClass(square.nativeElement, 'selected-square');
          this.renderer.removeClass(square.nativeElement, POSSIBLE_MOVE_SQUARE_CLASS);
          let position: Position = JSON.parse(square.nativeElement.dataset.position);

          if (position.x === selectedDisk.currentPosition.x && position.y === selectedDisk.currentPosition.y) {
            selectedSquares.push(square);
          }
          let moveToDifferentSquare = movesForDisk.find((move: Move) => {
            return move.destination.x === position.x && move.destination.y === position.y;
          });
          if (moveToDifferentSquare) {
            possibleTargetSquares.push(square);
          }
        });

        selectedSquares.forEach(square => {
          this.renderer.addClass(square.nativeElement, 'selected-square');
        });
        possibleTargetSquares.forEach(square => {
          this.renderer.addClass(square.nativeElement, POSSIBLE_MOVE_SQUARE_CLASS);
        });
      });
  }

  ngOnInit() {
  }

  ngOnDestroy(): void {
    this.unsubscribe.next();
    this.unsubscribe.complete();
  }

  selectDisk(disk: DiskComponent) {
    this.diskEvents$.emit({disk: disk.disk, type: 'SELECT'});
  }

  lock() {
    this.renderer.addClass(this.ref.nativeElement, 'locked');
  }

  unlock() {
    this.renderer.removeClass(this.ref.nativeElement, 'locked');
  }

  moveDiskToSquare(square) {
    let nativeElement = square._element.nativeElement;

    if (this.isPossibleMoveSquare(square)) {
      let position: Position = JSON.parse(nativeElement.dataset.position);
      let selectedDisk = this.selectedDisk;
      let move: Move = {destination: position, disk: selectedDisk, from: selectedDisk.currentPosition};

      this.diskEvents$.emit({disk: selectedDisk, move, type: 'MOVE'});
    }
  }

  isPossibleMoveSquare(square) {
    let nativeElement = square._element.nativeElement;
    let classList = nativeElement.className;

    return classList.includes(POSSIBLE_MOVE_SQUARE_CLASS);
  }
}
