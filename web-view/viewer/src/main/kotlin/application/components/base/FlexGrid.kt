package application.components.base

import application.ReactUtilities.appendChildren
import application.ReactUtilities.appendClassNames
import csstype.*
import emotion.react.css
import react.FC
import react.PropsWithChildren
import react.PropsWithClassName
import react.dom.html.ReactHTML.div

object FlexGrid {
  interface GridElementProps: PropsWithChildren, PropsWithClassName

  val flexContainer = FC<GridElementProps> { props ->
    div {
      css {
        display = Display.flex
      }
      props.className?.let { appendClassNames(it) }
      appendChildren(props)
    }
  }

  val column = FC<GridElementProps> { props ->
    flexContainer {
      css {
        flexDirection = FlexDirection.column
      }
      props.className?.let { appendClassNames(it) }
      appendChildren(props)
    }
  }

  val fullColumn = FC<GridElementProps> { props ->
    column {
      css {
        flexGrow = number(1.0)
        flexShrink = number(1.0)
        flexBasis = 0.pct
      }
      props.className?.let { appendClassNames(it) }
      appendChildren(props)
    }
  }

  val row = FC<GridElementProps> { props ->
    flexContainer {
      css {
        flexDirection = FlexDirection.row
      }
      props.className?.let { appendClassNames(it) }
      appendChildren(props)
    }
  }
}
