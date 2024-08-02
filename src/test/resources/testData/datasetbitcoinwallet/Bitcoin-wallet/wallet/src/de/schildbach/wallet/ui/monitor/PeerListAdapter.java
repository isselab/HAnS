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

package de.schildbach.wallet.ui.monitor;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.bitcoinj.core.Peer;
import org.bitcoinj.core.VersionMessage;

import de.schildbach.wallet.R;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.recyclerview.extensions.ListAdapter;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * @author Andreas Schildbach
 */
public class PeerListAdapter extends ListAdapter<PeerListAdapter.ListItem, PeerListAdapter.ViewHolder> {
    public static List<ListItem> buildListItems(final Context context, final List<Peer> peers,
            final Map<InetAddress, String> hostnames) {
        final List<ListItem> items = new ArrayList<>(peers.size());
        for (final Peer peer : peers)
            items.add(new ListItem(context, peer, hostnames));
        return items;
    }

    public static class ListItem {
        public ListItem(final Context context, final Peer peer, final Map<InetAddress, String> hostnames) {
            this.ip = peer.getAddress().getAddr();
            this.hostname = hostnames.get(ip);
            this.height = peer.getBestHeight();
            final VersionMessage versionMessage = peer.getPeerVersionMessage();
            this.version = versionMessage.subVer;
            this.protocol = "protocol: " + versionMessage.clientVersion;
            final long pingTime = peer.getPingTime();
            this.ping = pingTime < Long.MAX_VALUE ? context.getString(R.string.peer_list_row_ping_time, pingTime)
                    : null;
            this.isDownloading = peer.isDownloadData();
        }

        public final InetAddress ip;
        public final String hostname;
        public final long height;
        public final String version;
        public final String protocol;
        public final String ping;
        public final boolean isDownloading;
    }

    private final LayoutInflater inflater;

    public PeerListAdapter(final Context context) {
        super(new DiffUtil.ItemCallback<ListItem>() {
            @Override
            public boolean areItemsTheSame(final ListItem oldItem, final ListItem newItem) {
                return oldItem.ip.equals(newItem.ip);
            }

            @Override
            public boolean areContentsTheSame(final ListItem oldItem, final ListItem newItem) {
                if (!Objects.equals(oldItem.hostname, newItem.hostname))
                    return false;
                if (!Objects.equals(oldItem.ping, newItem.ping))
                    return false;
                if (!Objects.equals(oldItem.isDownloading, newItem.isDownloading))
                    return false;
                return true;
            }
        });

        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        return new ViewHolder(inflater.inflate(R.layout.peer_list_row, parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final ListItem listItem = getItem(position);
        holder.ipView.setText(listItem.hostname != null ? listItem.hostname : listItem.ip.getHostAddress());
        holder.heightView.setText(listItem.height > 0 ? listItem.height + " blocks" : null);
        holder.heightView.setTypeface(listItem.isDownloading ? Typeface.DEFAULT_BOLD : Typeface.DEFAULT);
        holder.versionView.setText(listItem.version);
        holder.versionView.setTypeface(listItem.isDownloading ? Typeface.DEFAULT_BOLD : Typeface.DEFAULT);
        holder.protocolView.setText(listItem.protocol);
        holder.protocolView.setTypeface(listItem.isDownloading ? Typeface.DEFAULT_BOLD : Typeface.DEFAULT);
        holder.pingView.setText(listItem.ping);
        holder.pingView.setTypeface(listItem.isDownloading ? Typeface.DEFAULT_BOLD : Typeface.DEFAULT);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView ipView;
        private final TextView heightView;
        private final TextView versionView;
        private final TextView protocolView;
        private final TextView pingView;

        private ViewHolder(final View itemView) {
            super(itemView);
            ipView = (TextView) itemView.findViewById(R.id.peer_list_row_ip);
            heightView = (TextView) itemView.findViewById(R.id.peer_list_row_height);
            versionView = (TextView) itemView.findViewById(R.id.peer_list_row_version);
            protocolView = (TextView) itemView.findViewById(R.id.peer_list_row_protocol);
            pingView = (TextView) itemView.findViewById(R.id.peer_list_row_ping);
        }
    }
}