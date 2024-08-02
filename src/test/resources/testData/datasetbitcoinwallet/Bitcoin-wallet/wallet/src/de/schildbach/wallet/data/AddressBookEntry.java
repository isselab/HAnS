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

package de.schildbach.wallet.data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

//&begin[AddressBook]
/**
 * @author Andreas Schildbach
 */
@Entity(tableName = "address_book")
public class AddressBookEntry {
    @NonNull
    @PrimaryKey
    @ColumnInfo(name = "address")
    private String address;

    @ColumnInfo(name = "label")
    private String label;

    public AddressBookEntry(final String address, final String label) {
        this.address = address;
        this.label = label;
    }

    public String getAddress() {
        return address;
    }

    public String getLabel() {
        return label;
    }

    public static Map<String, AddressBookEntry> asMap(final List<AddressBookEntry> entries) {
        if (entries == null)
            return null;
        final Map<String, AddressBookEntry> addressBook = new HashMap<>();
        for (final AddressBookEntry entry : entries)
            addressBook.put(entry.getAddress(), entry);
        return addressBook;
    }
}
//&end[AddressBook]