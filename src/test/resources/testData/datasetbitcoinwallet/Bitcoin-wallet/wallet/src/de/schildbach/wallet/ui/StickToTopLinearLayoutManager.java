/*
 * Copyright the original author or authors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package de.schildbach.wallet.ui;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

/**
 * @author Andreas Schildbach
 */
public class StickToTopLinearLayoutManager extends LinearLayoutManager {
    public StickToTopLinearLayoutManager(final Context context) {
        super(context);
    }

    @Override
    public void onItemsAdded(final RecyclerView recyclerView, final int positionStart, final int itemCount) {
        super.onItemsAdded(recyclerView, positionStart, itemCount);
        if (positionStart == 0 && findFirstCompletelyVisibleItemPosition() <= itemCount)
            scrollToPosition(0);
    }
}