package de.digitalstep.ntlmproxy.ui

import javax.swing.event.DocumentListener
import javax.swing.event.DocumentEvent
import javax.swing.SwingUtilities
import javax.swing.text.{ Document, Element }

class LimitLinesDocumentListener(
	val maximumLines: Int,
	val isRemoveFromStart: Boolean) extends DocumentListener {

	def insertUpdate(e: DocumentEvent) {
		SwingUtilities.invokeLater(new Runnable() {
			def run {
				removeLines(e)
			}
		});
		// TODO
	}

	def removeLines(e: DocumentEvent) {
		val document = e.getDocument
		val root = document.getDefaultRootElement
		while (root.getElementCount() > maximumLines) {
			if (!isRemoveFromStart) {
				removeFromStart(document, root)
			} else {
				removeFromEnd(document, root)
			}
		}
	}

	def removeFromStart(document: Document, root: Element) {
		val line = root.getElement(0)
		val end = line.getEndOffset
		document.remove(0, end)
	}

	def removeFromEnd(document: Document, root: Element) {
		val line = root.getElement(root.getElementCount() - 1)
		val start = line.getStartOffset
		val end = line.getEndOffset

		document.remove(start - 1, end - start)
	}

	def changedUpdate(e: DocumentEvent) {
		// TODO
	}

	def removeUpdate(e: DocumentEvent) {
		// TODO
	}

}